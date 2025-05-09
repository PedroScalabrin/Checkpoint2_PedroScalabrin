package carreiras.com.github.cryptomonitor

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import carreiras.com.github.cryptomonitor.model.TickerResponse
import carreiras.com.github.cryptomonitor.service.MercadoBitcoinServiceFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurando a toolbar
        val toolbarMain: Toolbar = findViewById(R.id.toolbar_main)
        configureToolbar(toolbarMain)

        // Configurando o botão Refresh
        val btnRefresh: Button = findViewById(R.id.btn_refresh)
        btnRefresh.setOnClickListener {
            makeRestCall()
        }
    }

    private fun configureToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(getColor(R.color.white))
        supportActionBar?.setTitle(getText(R.string.app_title))
        supportActionBar?.setBackgroundDrawable(getDrawable(R.color.primary))
    }

    private fun makeRestCall() {
        // Mostrar indicador de carregamento
        val progressBar: ProgressBar = findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE
        
        // Usar Dispatchers.IO para operações de rede
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = MercadoBitcoinServiceFactory().create()
                val response = service.getTicker()
                
                // Voltar para a thread principal para atualizar a UI
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    
                    if (response.isSuccessful) {
                        val tickerResponse = response.body()

                        // Atualizando os componentes TextView
                        val lblValue: TextView = findViewById(R.id.lbl_value)
                        val lblDate: TextView = findViewById(R.id.lbl_date)
                        val lblCrypto: TextView = findViewById(R.id.lbl_crypto)

                        // Definir o nome da criptomoeda
                        lblCrypto.text = "Bitcoin (BTC)"

                        val lastValue = tickerResponse?.ticker?.last?.toDoubleOrNull()
                        if (lastValue != null) {
                            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                            lblValue.text = numberFormat.format(lastValue)
                        }

                        val date = tickerResponse?.ticker?.date?.let { Date(it * 1000L) }
                        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        lblDate.text = sdf.format(date)
                        
                        // Salvar dados em cache para uso offline
                        saveCacheData(tickerResponse)

                    } else {
                        // Trate o erro de resposta não bem-sucedida
                        val errorMessage = when (response.code()) {
                            400 -> "Bad Request"
                            401 -> "Unauthorized"
                            403 -> "Forbidden"
                            404 -> "Not Found"
                            else -> "Erro desconhecido: ${response.code()}"
                        }
                        Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                        
                        // Tentar carregar dados do cache em caso de erro
                        loadCacheData()
                    }
                }
            } catch (e: Exception) {
                // Voltar para a thread principal para mostrar erro
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Falha na chamada: ${e.message}", Toast.LENGTH_LONG).show()
                    
                    // Tentar carregar dados do cache em caso de erro
                    loadCacheData()
                }
            }
        }
    }
    
    // Método para salvar dados em cache
    private fun saveCacheData(tickerResponse: TickerResponse?) {
        tickerResponse?.let {
            val sharedPrefs = getSharedPreferences("crypto_cache", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("btc_last", it.ticker.last)
            editor.putLong("btc_date", it.ticker.date)
            editor.apply()
        }
    }
    
    // Método para carregar dados do cache
    private fun loadCacheData() {
        val sharedPrefs = getSharedPreferences("crypto_cache", Context.MODE_PRIVATE)
        val lastValue = sharedPrefs.getString("btc_last", null)?.toDoubleOrNull()
        val date = sharedPrefs.getLong("btc_date", 0)
        
        if (lastValue != null && date > 0) {
            val lblValue: TextView = findViewById(R.id.lbl_value)
            val lblDate: TextView = findViewById(R.id.lbl_date)
            val lblCrypto: TextView = findViewById(R.id.lbl_crypto)
            
            // Definir o nome da criptomoeda
            lblCrypto.text = "Bitcoin (BTC) - Offline"
            
            val numberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            lblValue.text = numberFormat.format(lastValue)
            
            val dateObj = Date(date * 1000L)
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            lblDate.text = "${sdf.format(dateObj)} (cache)"
            
            Toast.makeText(this, "Dados carregados do cache", Toast.LENGTH_SHORT).show()
        }
    }
}