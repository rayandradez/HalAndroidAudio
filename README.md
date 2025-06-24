# 🚀 Android Audio HAL Simulation & Energy Profiling

## 🧑‍💻 Créditos

*   **Desenvolvedores:**
    *   **Alisson Freitas**
    *   **Eduardo Perez Uanús**
    *   **João Gabriel A. Gomes Alves**
    *   **Rayanne da Silva Andrade**
*   **Instituição:** Centro Universitário SENAI CIMATEC
*   **Curso:** Desenvolvimento de Sistemas Embarcados - Android

Este repositório contém uma atividade prática de desenvolvimento Android focada na compreensão e simulação de uma Hardware Abstraction Layer (HAL) de áudio, bem como na análise e otimização do consumo de energia em dispositivos móveis. O projeto explora a interação entre código nativo (C++) e a camada gerenciada (Kotlin) via Java Native Interface (JNI), utilizando ferramentas de perfilamento e depuração do Android Studio.

## 🎯 Objetivo da Atividade

O principal objetivo desta atividade é:
*   Desenvolver uma HAL de áudio simplificada em C++.
*   Integrar essa HAL a um aplicativo Android (Kotlin) usando JNI.
*   Simular falhas na HAL e diagnosticar problemas usando Logcat.
*   Analisar o consumo de CPU e energia da HAL utilizando o Android Studio Profiler.
*   Implementar e verificar técnicas de otimização de energia.
*   Documentar todo o processo e os resultados em um relatório técnico.

## ✨ Funcionalidades e Conceitos Explorados

*   **Implementação de HAL de Áudio (C++):** Criação de uma HAL simplificada que simula operações de áudio (`audio_open`, `audio_close`, `audio_write`).
*   **Java Native Interface (JNI):** Ponte de comunicação entre o código Kotlin do aplicativo e o código C++ da HAL.
*   **Android NDK (Native Development Kit):** Ferramentas para compilação de código C/C++ para Android.
*   **CMake:** Sistema de build para gerenciamento da compilação do código nativo.
*   **Android Studio Profiler:** Análise detalhada de uso de CPU e consumo de energia (Power Rails).
*   **Depuração com Logcat:** Utilização de logs para rastrear o fluxo de execução e diagnosticar problemas em tempo real.
*   **Simulação de Falhas:** Injeção de erros controlados para testar a resiliência da HAL.
*   **Otimização de Energia:** Aplicação de técnicas simples (ex: `usleep`) para reduzir o consumo de CPU.

## 🛠️ Tecnologias Utilizadas

*   **Linguagens:** Kotlin, C++
*   **IDE:** Android Studio
*   **Build System:** Gradle, CMake
*   **Ferramentas:** Android NDK, Android Studio Profiler, `adb logcat`, `adb shell dumpsys`

## 🚀 Como Configurar e Executar o Projeto

### Pré-requisitos

*   Android Studio instalado.
*   Java Development Kit (JDK) configurado.
*   Um emulador Android (API 35 ou superior) ou dispositivo físico.

### Configuração Crucial do NDK

Durante o desenvolvimento desta atividade, foram encontrados **desafios significativos e persistentes** na configuração do NDK no Android Studio, que resultaram em erros de "file not found" para cabeçalhos padrão do NDK (`hardware/hardware.h`, `log/log.h`). Para contornar esses problemas e garantir a compilação, foi necessário um processo de configuração manual e a injeção de definições de cabeçalho.

**Versão do NDK Recomendada/Utilizada:** `r25 (27.0.12077973)`

**Passos para a Configuração do NDK (Altamente Recomendado):**

1.  **Feche o Android Studio completamente.**
2.  **Limpeza Manual do NDK:** Navegue até o diretório do seu Android SDK (geralmente `C:\Users\SEU_USUARIO\AppData\Local\Android\Sdk`) e **exclua a pasta `ndk` inteira**.
3.  **Reinicie o computador.**
4.  **Download Manual do NDK:** Baixe a versão `r25c` (ou a mais recente do `r25`) do NDK diretamente do site oficial do Android NDK: [https://developer.android.com/ndk/downloads/](https://developer.android.com/ndk/downloads/) (Procure por "Archived NDK releases" e a versão `r25c`).
5.  **Extraia o NDK:** Descompacte o arquivo `.zip` baixado para uma pasta (ex: `android-ndk-r25c`).
6.  **Mova o NDK:** Copie (ou recorte) a pasta `android-ndk-r25c` e cole-a **diretamente dentro do seu diretório Android SDK** (ex: `C:\Users\SEU_USUARIO\AppData\Local\Android\Sdk\android-ndk-r25c`).
7.  **Configure o NDK no Android Studio:**
    *   Abra o Android Studio.
    *   Vá em `File` > `Project Structure...` > `SDK Location`.
    *   Na seção "Android NDK location", clique em `Edit` ou no ícone de pasta e selecione o caminho para a pasta `android-ndk-r25c` que você colou (ex: `C:\Users\SEU_USUARIO\AppData\Local\Android\Sdk\android-ndk-r25c`).
    *   Clique `OK` para aplicar as mudanças.
8.  **Forçe a Versão do NDK no `build.gradle`:**
    *   Abra o arquivo `app/build.gradle` (Module: app).
    *   Dentro do bloco `android { ... }`, na seção `defaultConfig { ... }`, adicione ou atualize a linha `ndkVersion` para:
        ```gradle
        android {
            // ...
            defaultConfig {
                // ...
                ndkVersion "27.0.12077973" // Garante que a versão r25 seja usada
            }
            // ...
        }
        ```
    *   **Importante:** Remova a linha `ndkVersion` se ela estiver presente em outros arquivos `build.gradle` de módulos ou no `build.gradle` do projeto.

### Instalação de SDKs e Build-Tools

Certifique-se de que os seguintes componentes do Android SDK estão instalados via SDK Manager (`Tools` > `SDK Manager`):
*   **SDK Platforms:** Android 15 (API Level 35)
*   **SDK Tools:**
    *   Android SDK Build-Tools (Versão 35 ou superior)
    *   Android SDK Platform-Tools
    *   CMake
    *   NDK (Side by side) - *Embora você tenha instalado manualmente, o SDK Manager ainda pode listar e gerenciar outras versões. A configuração no `build.gradle` é que força a versão específica.*

### Compilando e Executando

1.  **Sincronizar Projeto:** No Android Studio, vá em `File` > `Sync Project with Gradle Files`.
2.  **Limpar Projeto:** Vá em `Build` > `Clean Project`.
3.  **Reconstruir Projeto:** Vá em `Build` > `Rebuild Project`.
4.  **Executar no Emulador/Dispositivo:** Clique no botão `Run 'app'` (o triângulo verde na barra de ferramentas) para instalar e iniciar o aplicativo em seu emulador ou dispositivo conectado.

### Verificando a Funcionalidade

1.  **Abra o Logcat:** No Android Studio, vá em `View` > `Tool Windows` > `Logcat`.
2.  **Defina o Filtro:** Use o filtro `tag:AudioHAL|NativeJNI|AudioTest|AudioHAL_App` para ver apenas os logs relevantes do seu aplicativo e da HAL.
3.  **Interaja com o Aplicativo:**
    *   A interface simples do aplicativo contém um botão "Reproduzir Som (via HAL Simulada)".
    *   Clique no botão.
    *   **Observe o Logcat:** Você deverá ver uma sequência de logs detalhando a chamada do Kotlin para a JNI, a inicialização e o "processamento" da HAL, e o feedback do SoundPool.
    *   **Exemplo de Logs Esperados:**
        ```
        D/AudioHAL_App: MainActivity: Botão 'Reproduzir Som' clicado. Chamando HAL...
        D/NativeJNI: JNI: Tentando chamar triggerHalAudioWrite...
        D/NativeJNI: JNI: Módulo de áudio HAL obtido via referência direta.
        D/AudioHAL: AudioHAL: Dispositivo de áudio inicializado com sucesso.
        D/NativeJNI: JNI: Dispositivo de áudio HAL aberto com sucesso.
        D/AudioHAL: AudioHAL: Processando 1024 bytes de áudio.
        D/NativeJNI: JNI: Função audio_write da HAL chamada. Bytes processados: 1024
        D/AudioHAL_App: Chamada HAL de áudio concluída com sucesso!
        D/AudioTest: playTestSound: Iniciando carregamento do som.
        D/AudioTest: playTestSound: Som carregado com sucesso, tentando tocar.
        D/AudioTest: playTestSound: Som reproduzido via SoundPool (não HAL direta)
        ```
    *   **Verifique o Status:** O `TextView` abaixo do botão na UI deve ser atualizado para "Status: HAL de áudio chamada com sucesso!".

### Analisando Consumo de Energia e CPU

1.  **Abra o Profiler:** No Android Studio, vá em `View` > `Tool Windows` > `Profiler`.
2.  **Selecione o Processo:** Escolha seu emulador/dispositivo e o processo `com.example.myaudiohalproject`.
3.  **Inicie a Gravação:** Clique no botão de gravação (círculo vermelho) na seção `CPU` ou `Energy`.
4.  **Interaja:** Clique repetidamente no botão "Reproduzir Som (via HAL Simulada)" no seu aplicativo.
5.  **Analise:**
    *   **CPU:** Observe os gráficos de uso da CPU e atividade das threads. Você verá picos de atividade quando a HAL é acionada.
    *   **Energy:** Na aba `Energy`, observe os "Power Rails" (CPU Big, CPU Little, etc.) para correlacionar a atividade da sua HAL com o consumo de energia.
    *   **Otimização:** Após implementar o `usleep(1000)` em `audio_hal.cpp` (conforme o Passo 8 da atividade), compare os gráficos do Profiler antes e depois para demonstrar a redução no consumo de CPU em estado ocioso.


## 🤝 Contribuições

Este projeto foi desenvolvido como parte de uma atividade acadêmica. Contribuições são bem-vindas para aprimoramento e demonstração de conceitos.

