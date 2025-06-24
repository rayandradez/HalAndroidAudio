# Para mais informações sobre como usar CMake com Android Studio, consulte a
# documentação oficial: https://d.android.com/studio/projects/add-native-code.html.
# Para mais exemplos de uso do CMake, veja https://github.com/android/ndk-samples.

# Define a versão mínima do CMake necessária para este projeto.
# É importante usar uma versão compatível com o Android Studio e o NDK.
cmake_minimum_required(VERSION 3.22.1)

# Declara o nome do projeto CMake.
# Este nome pode ser acessado via ${ PROJECT_NAME} ou ${CMAKE_PROJECT_NAME}.
project("myaudiohalproject")

# --- Definição da Biblioteca Nativa ---
# Cria e nomeia uma biblioteca compartilhada (.so), que será empacotada com o APK.
# Você pode definir múltiplas bibliotecas aqui, e o CMake as construirá.
# Para carregar esta biblioteca no seu app Java/Kotlin, você deve chamar
# System.loadLibrary() e passar o nome definido aqui (ex: "native-lib").
add_library( # Define o nome da biblioteca que será gerada.
        native-lib

        # Define o tipo da biblioteca como compartilhada (SHARED).
        # Bibliotecas compartilhadas são arquivos .so que podem ser carregados em tempo de execução.
        SHARED

        # Fornece os caminhos relativos para os arquivos fonte da sua biblioteca.
        # Ambos os arquivos C++ do projeto são incluídos aqui.
        native-lib.cpp   # O arquivo que contém a interface JNI
        audio_hal.cpp )  # O arquivo que contém a implementação da HAL de áudio simulada

# --- Encontrando e Vinculando Bibliotecas do NDK ---
# Esta seção garante que as bibliotecas necessárias do Android NDK sejam encontradas e vinculadas.

# Encontra a biblioteca de log do NDK.
# A biblioteca 'log' é necessária para usar as funções de log do Android (ALOGD, ALOGE, etc.).
find_library( # Define o nome da variável CMake que armazenará o caminho da biblioteca.
        log-lib

        # Especifica o nome da biblioteca do NDK que o CMake deve localizar.
        # 'log' refere-se à biblioteca 'liblog.so'.
        log )

# Vincula a biblioteca nativa do seu projeto com as bibliotecas do sistema.
# Isso garante que as funções chamadas no seu código C++ (como as de log)
# sejam resolvidas corretamente em tempo de linkagem.
target_link_libraries( # Especifica a biblioteca alvo para vincular.
        native-lib  # A sua biblioteca principal (definida acima)

        # Vincula a biblioteca alvo à biblioteca de log do NDK.
        # Isso também adiciona automaticamente os caminhos de inclusão necessários para seus cabeçalhos.
        ${log-lib}

        # Adicione outras bibliotecas do sistema se forem necessárias.
        # Por exemplo, 'dl' (para carregamento dinâmico) ou 'm' (para funções matemáticas).
        # Para esta atividade, apenas 'log' é estritamente necessário.
        # dl # Exemplo: para dlopen/dlsym, se fosse carregar módulos dinamicamente
)
