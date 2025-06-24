package com.example.myaudiohalproject

import android.content.Context
import android.media.AudioManager // Importado, mas não usado diretamente no SoundPool simples
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity é a principal tela do aplicativo Android.
 * Ela serve como a interface entre o usuário e a HAL de áudio simulada,
 * utilizando a Java Native Interface (JNI) para se comunicar com o código C++.
 */
class MainActivity : AppCompatActivity() {

    // --- Declaração do Método Nativo (JNI) ---
    /**
     * Declara um método nativo (implementado em C++) que será chamado a partir do Kotlin.
     * Este método é o ponto de entrada para a interação com a HAL de áudio simulada.
     *
     * A convenção de nomenclatura JNI para métodos nativos é:
     * Java_NomeDoPacote_NomeDaClasse_NomeDoMetodo
     * Ex: Java_com_example_myaudiohalproject_MainActivity_triggerHalAudioWrite
     *
     * @return Um Int que representa o código de retorno da função HAL (0 para sucesso, outros para erro).
     */
    private external fun triggerHalAudioWrite(): Int

    // --- Carregamento da Biblioteca Nativa ---
    /**
     * Bloco estático que é executado quando a classe MainActivity é carregada pela JVM.
     * Ele carrega a biblioteca nativa (.so) que contém o código C++ (native-lib.cpp e audio_hal.cpp).
     * O nome da biblioteca ('native-lib') deve corresponder ao nome definido em CMakeLists.txt.
     */
    companion object {
        init {
            try {
                // Carrega a biblioteca nativa 'native-lib'.
                // Se o carregamento falhar (ex: biblioteca não encontrada), uma exceção é lançada.
                System.loadLibrary("native-lib")
                Log.d("MainActivity", "Biblioteca nativa 'native-lib' carregada com sucesso.")
            } catch (e: UnsatisfiedLinkError) {
                // Registra um erro se a biblioteca não puder ser carregada.
                Log.e("MainActivity", "Falha ao carregar a biblioteca nativa 'native-lib': ${e.message}")
                // Em um app de produção, você poderia exibir uma mensagem de erro ao usuário aqui.
            }
        }
    }

    // --- Componentes da UI ---
    // Declaração de uma variável para o TextView que exibirá o status da operação.
    private lateinit var statusTextView: TextView

    // --- Ciclo de Vida da Atividade: onCreate() ---
    /**
     * Chamado quando a atividade é criada pela primeira vez.
     * É aqui que a interface do usuário é configurada e os listeners de eventos são inicializados.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Log para indicar que o método onCreate foi iniciado, útil para depuração.
        Log.d("AudioHAL_App", "MainActivity: onCreate iniciado.")
        // Define o layout da atividade a partir do arquivo activity_main.xml.
        setContentView(R.layout.activity_main)

        // Inicializa o botão e o TextView a partir de seus IDs no layout.
        val btnPlay: Button = findViewById(R.id.btnPlay)
        statusTextView = findViewById(R.id.statusTextView)

        // --- Configuração do Listener do Botão ---
        /**
         * Define um listener de clique para o botão "Reproduzir Som".
         * Quando o botão é clicado, ele aciona a chamada à HAL simulada e reproduz um som.
         */
        btnPlay.setOnClickListener {
            // Log para indicar que o botão foi clicado.
            Log.d("AudioHAL_App", "MainActivity: Botão 'Reproduzir Som' clicado. Chamando HAL...")
            // Atualiza o status na UI para informar o usuário sobre a operação em andamento.
            updateStatus("Chamando HAL...")

            // 1. Chama a função nativa (JNI) que interage com a HAL de áudio simulada.
            val result = triggerHalAudioWrite()

            // 2. Verifica o resultado da chamada à HAL e atualiza o status na UI e nos logs.
            if (result == 0) { // O código 0 geralmente indica sucesso em funções C/C++.
                Log.d("AudioHAL_App", "Chamada HAL de áudio concluída com sucesso!")
                updateStatus("HAL de áudio chamada com sucesso!")
            } else {
                Log.e("AudioHAL_App", "Erro na chamada HAL de áudio: $result")
                updateStatus("Erro na chamada HAL de áudio: $result")
            }

            // 3. Chama a função playTestSound() para fornecer feedback audível.
            // Esta chamada não interage diretamente com a HAL, mas simula a reprodução de um som real
            // usando as APIs de áudio do Android (SoundPool), como sugerido na atividade.
            playTestSound()
        }

        // --- Exemplo de AudioManager (não usado diretamente neste código simplificado) ---
        // A atividade menciona AudioManager, mas SoundPool não o usa diretamente para reprodução básica.
        // private lateinit var audioManager: AudioManager
        // audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    // --- Função para Reproduzir Som de Teste (SoundPool) ---
    /**
     * Toca um som de teste usando a classe SoundPool do Android.
     * SoundPool é ideal para sons curtos e de baixa latência (ex: efeitos sonoros).
     *
     * NOTA: Esta função REPRODUZ um som real no dispositivo, mas NÃO interage diretamente
     * com a sua HAL de áudio simulada. Ela serve como um feedback audível para o usuário.
     */
    private fun playTestSound() {
        // Log para indicar o início do carregamento do som.
        Log.d("AudioTest", "playTestSound: Iniciando carregamento do som.")

        // Cria uma instância de SoundPool.
        // SoundPool.Builder().build() é a forma moderna de inicializá-lo.
        val soundPool = SoundPool.Builder().build()

        // Carrega o arquivo de áudio.
        // O arquivo R.raw.test_sound deve existir na pasta app/src/main/res/raw/.
        // Por exemplo, app/src/main/res/raw/test_sound.wav ou test_sound.mp3.
        val soundId = soundPool.load(this, R.raw.test_sound, 1) // Prioridade 1

        // Define um listener que será chamado quando o som for carregado.
        // A reprodução só deve ocorrer APÓS o carregamento completo para evitar erros.
        soundPool.setOnLoadCompleteListener { _, _, _ ->
            // Log para indicar que o som foi carregado e está prestes a ser reproduzido.
            Log.d("AudioTest", "playTestSound: Som carregado com sucesso, tentando tocar.")
            // Reproduz o som.
            soundPool.play(
                soundId,    // ID do som carregado
                1.0f,       // volume esquerdo (0.0 a 1.0)
                1.0f,       // volume direito (0.0 a 1.0)
                0,          // prioridade (0 é a mais baixa)
                0,          // loop (0 = não repete, -1 = loop infinito)
                1.0f        // taxa de reprodução (1.0 = normal)
            )
            // Log final para confirmar a reprodução do som via SoundPool.
            Log.d("AudioTest", "playTestSound: Som reproduzido via SoundPool (não HAL direta)")

            // Opcional: Libere o SoundPool após um curto atraso para economizar recursos,
            // especialmente se o som não for ser reproduzido novamente em breve.
            // Handler(Looper.getMainLooper()).postDelayed({ soundPool.release() }, 1000)
        }
    }

    // --- Função para Atualizar o Status na UI ---
    /**
     * Atualiza o texto do TextView de status na thread principal da UI.
     * Garante que as atualizações da UI sejam seguras e visíveis.
     * @param message A string a ser exibida como status.
     */
    private fun updateStatus(message: String) {
        // Certifica-se de que a atualização da UI ocorre na thread principal.
        runOnUiThread {
            statusTextView.text = "Status: $message"
        }
    }
}
