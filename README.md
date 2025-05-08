# Crypto Monitor - Aplicativo Android

## Visão Geral

O Crypto Monitor é um aplicativo Android desenvolvido em Kotlin que permite aos usuários monitorar o preço atual do Bitcoin em tempo real. O aplicativo consome a API do Mercado Bitcoin para obter as informações mais recentes sobre o valor da criptomoeda.

## Funcionalidades

- Exibição do valor atual do Bitcoin em Reais (BRL)
- Atualização dos dados em tempo real através de botão de refresh
- Interface simples e intuitiva
- Tratamento de erros para falhas de conexão

## Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **Plataforma**: Android
- **Versão mínima do SDK**: 27 (Android 8.1)
- **Versão alvo do SDK**: 34 (Android 14)
- **Bibliotecas**:
  - Retrofit: Para consumo de APIs REST
  - Coroutines: Para operações assíncronas
  - AndroidX: Para componentes de UI compatíveis
  - AppCompat: Para compatibilidade com versões anteriores do Android

## Estrutura do Projeto

```
app/
├── src/
│   ├── main/
│   │   ├── java/carreiras/com/github/cryptomonitor/
│   │   │   ├── model/
│   │   │   │   └── TicketResponse.kt
│   │   │   ├── service/
│   │   │   │   ├── MercadoBitcoinService.kt
│   │   │   │   └── MercadoBitcoinServiceFactory.kt
│   │   │   ├── ui/
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   └── AndroidManifest.xml
```

## Arquitetura

O aplicativo segue uma arquitetura simples baseada em componentes:

- **Model**: Contém as classes de dados que representam as respostas da API
- **Service**: Contém as interfaces e classes responsáveis pela comunicação com a API
- **UI**: Contém os componentes de interface do usuário

## Como Usar

1. Clone o repositório
2. Abra o projeto no Android Studio
3. Execute o aplicativo em um emulador ou dispositivo físico
4. Clique no botão "Refresh" para atualizar o valor do Bitcoin

## Permissões

O aplicativo requer a seguinte permissão:

- `android.permission.INTERNET`: Para acessar a API do Mercado Bitcoin

## Explicação do Código

### MainActivity.kt

A `MainActivity` é o ponto de entrada principal do aplicativo e contém a lógica para exibir e atualizar os dados do Bitcoin.

```kotlin
// Método onCreate - Inicializa a atividade
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
```

Este método é chamado quando a atividade é criada. Ele configura a interface do usuário, inicializa a toolbar e define o comportamento do botão de atualização.

```kotlin
// Método configureToolbar - Configura a aparência da toolbar
private fun configureToolbar(toolbar: Toolbar) {
    setSupportActionBar(toolbar)
    toolbar.setTitleTextColor(getColor(R.color.white))
    supportActionBar?.setTitle(getText(R.string.app_title))
    supportActionBar?.setBackgroundDrawable(getDrawable(R.color.primary))
}
```

Este método personaliza a aparência da toolbar, definindo cores, título e estilo.

```kotlin
// Método makeRestCall - Realiza a chamada à API e atualiza a interface
private fun makeRestCall() {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val service = MercadoBitcoinServiceFactory().create()
            val response = service.getTicker()
        
            // Processamento da resposta e atualização da UI
            // ...
        } catch (e: Exception) {
            // Tratamento de erros
        }
    }
}
```

Este método é responsável por fazer a chamada à API do Mercado Bitcoin usando coroutines para operações assíncronas. Ele processa a resposta e atualiza a interface do usuário com os dados recebidos, além de tratar possíveis erros.

### Model - TickerResponse.kt

As classes de modelo representam a estrutura de dados retornada pela API:

```kotlin
// Classe TickerResponse - Representa a resposta completa da API
class TickerResponse(
    val ticker: Ticker
)

// Classe Ticker - Contém os dados do Bitcoin
class Ticker(
    val high: String,  // Maior preço nas últimas 24h
    val low: String,    // Menor preço nas últimas 24h
    val vol: String,    // Volume negociado nas últimas 24h
    val last: String,   // Preço da última negociação
    val buy: String,    // Maior preço de compra das últimas 24h
    val sell: String,   // Menor preço de venda das últimas 24h
    val date: Long      // Timestamp da informação
)
```

Estas classes são usadas pelo Retrofit para deserializar automaticamente a resposta JSON da API em objetos Kotlin.

### Service - MercadoBitcoinService.kt

A interface de serviço define os endpoints da API que serão consumidos:

```kotlin
// Interface MercadoBitcoinService - Define os endpoints da API
interface MercadoBitcoinService {
    @GET("api/BTC/ticker/")
    suspend fun getTicker(): Response<TickerResponse>
}
```

Esta interface utiliza anotações do Retrofit para definir o endpoint e o método HTTP. A função `getTicker()` é marcada como `suspend` para ser chamada dentro de uma coroutine.

### Service - MercadoBitcoinServiceFactory.kt

A fábrica de serviços é responsável por criar e configurar o cliente Retrofit:

```kotlin
// Classe MercadoBitcoinServiceFactory - Cria e configura o cliente Retrofit
class MercadoBitcoinServiceFactory {
    fun create(): MercadoBitcoinService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.mercadobitcoin.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MercadoBitcoinService::class.java)
    }
}
```

Esta classe configura o Retrofit com a URL base da API e o conversor Gson para processar as respostas JSON.

## Fluxo de Dados

1. O usuário clica no botão "Refresh"
2. O método `makeRestCall()` é chamado
3. Uma coroutine é iniciada para fazer a chamada assíncrona à API
4. O serviço Retrofit faz a requisição HTTP
5. A resposta é convertida automaticamente para objetos Kotlin
6. Os dados são formatados e exibidos na interface
7. Em caso de erro, uma mensagem é exibida ao usuário

## Melhorias Futuras

- Adicionar suporte para mais criptomoedas
- Implementar gráficos de variação de preço
- Adicionar notificações para alertas de preço
- Implementar tema escuro
- Adicionar suporte para diferentes moedas (USD, EUR, etc.)

## Autor

Pedro Scalabrin - RM98914
