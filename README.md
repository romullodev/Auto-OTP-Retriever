![](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![](https://img.shields.io/badge/Kotlin-0095D5?&style=for-the-badge&logo=kotlin&logoColor=white)

# Auto OTP Retriever
<img src="/giff/otp.gif" width="40%" height="40%"/>

Esta biblioteca facilita a leitura automática nas mensagens SMS de [códigos OTP](https://en.wikipedia.org/wiki/One-time_password) utilizando  a [API SMS Retriever](https://developers.google.com/identity/sms-retriever/overview?hl=pt-br). Além disso, a biblioteca oferece a opção de recuperar o número de telefone do usuário por meio do  [seletor de dica](https://developers.google.com/identity/sms-retriever/request?hl=pt-br). Vale destacar que, desde [janeiro de 2019](https://support.google.com/googleplay/android-developer/answer/10208820?hl=en&visit_id=638281555162903411-2872526064&rd=1), a Google restringiu o uso de permissões de alto risco ou confidenciais para a publicação de apps em sua [loja virtual](https://play.google.com/store/games?hl=pt_BR&gl=US), incluindo permissões SMS que seriam necessárias numa possível implementação custimizada para recuperar mensagens SMS do usuário. Com o SMS Retriever, todo o gerenciamento de permissões é feito diretamente pela própria API no momento oportuno, sem a necessidade de adicionar permissões de SMS diretamente no arquivo do manifesto.

## Instalação

### Adicione o repositório ```jitpack.io``` no build.gradle (app)

 ```groove
allprojects {
    repositories {
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

### Adicione a dependência da biblioteca

```groove
implementation 'com.github.romullodev:auto-otp-retriever:1.0.0'
```

## Utilização

Extenda sua Activity de ```AutoOtpRetrieverActivity```, fornecendo obrigatoriamente a implementação dos campos ```otpCodeListener``` e ```phoneNumberHintListener```. O número de telefone do usuário pode ser obtido através do método ```requestPhoneNumberHint``` em que chamará o listener ```phoneNumberHintListener```. Para leitura automática do código OTP, chame o método ```initializeAutoOtp``` para iniciar a tarefa de recuperação do código. Quando a mensagem SMS chegar no disposittivo, caso o usuário conceda permissão de leitura de SMS, o listener ```otpCodeListener``` será chamado. Abaixo, temos um exemplo da implementação:

```kotlin
class MainActivity : AutoOtpRetrieverActivity() {
    private lateinit var binding: ActivityMainBinding

    override val otpCodeListener: (String) -> Unit = { setupOtpListener(it) }
    override val phoneNumberHintListener: (String) -> Unit = { setupPhoneNumberListener(it) }

    // Optionals
    override val timeoutListener: () -> Unit get() = { Log.d("MainActivity", "timeout - otp retriever") }
    override val startListenSmsMessagesSuccessfully: () -> Unit get() = { Log.d("MainActivity", "listening for sms messages") }
    override val failureOnListenSmsMessages: () -> Unit get() = { Log.d("MainActivity", "failure on listening for sms messages") }
    override val registerOtpReceiverListener: () -> Unit get() = { Log.d("MainActivity", "opt receiver was registered") }
    override val unregisterOtpReceiverListener: () -> Unit get() = { Log.d("MainActivity", "opt receiver was unregistered") }

    private fun setupOtpListener(token: String) {
        binding.run {
            editTextOtp.setText(token)
            editTextOtp.setSelection(token.length)
        }
    }
    private fun setupPhoneNumberListener(phoneNumber: String) {
        binding.run {
            phoneNumberEditText.setText(phoneNumber)
            phoneNumberEditText.setSelection(phoneNumber.length)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
    }

    private fun setupListeners() {
        binding.run {
            buttonStartListening.setOnClickListener {
                buttonStartListening.text = "listening ..."
                initializeAutoOtp()
            }
            buttonPickePhone.setOnClickListener {
                requestPhoneNumberHint()
            }
        }
    }
}
```

### Campos opcionais:

#### timeoutListener
Caso nenhuma mensagem SMS com código OTP chegue no dispositivo dentro de 5 minutos após a chamada do método initializeAutoOtp, esse listener será acionado.
#### startListenSmsMessagesSuccessfully
Notifica que o app está aguardando com sucesso a leitura do código OTP
#### failureOnListenSmsMessages
Notifica a ocorrência de alguma falha logo após a chamada do método initializeAutoOtp
#### registerOtpReceiverListener
Notifica que o Broadcast de recuperação do OTP foi registrado
#### unregisterOtpReceiverListener
Notifica que registro do Broadcast supracitado foi cancelado

## Licença MIT

Copyright 2023 Rômulo Silva

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the “Software”), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.