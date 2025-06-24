#include <jni.h>
#include <string>
#include <android/log.h> // Para __android_log_print
#include <string.h>      // Para memset

// --- DEFINIÇÕES MANUAIS DAS MACROS ALOG ---
// Usaremos as mesmas definições que você já tem em audio_hal.cpp
#ifndef ALOGD
#define ALOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "NativeJNI", __VA_ARGS__)
#endif

#ifndef ALOGE
#define ALOGE(...) __android_log_print(ANDROID_LOG_ERROR, "NativeJNI", __VA_ARGS__)
#endif
// --- FIM DAS DEFINIÇÕES MANUAIS DAS MACROS ALOG ---


// --- DEFINIÇÕES MÍNIMAS INJETADAS PARA SIMULAÇÃO (IGUAIS ÀS DE audio_hal.cpp) ---
// Estas estruturas e macros substituem o que viria de <hardware/hardware.h> e <hardware/audio.h>
// A ORDEM DE DECLARAÇÃO É CRUCIAL AQUI!

// Forward declarations para estruturas que se referenciam mutuamente
struct hw_module_t;
struct hw_device_t;
struct audio_hw_device; // Forward declaration para audio_hw_device_t

// Common structure for all hardware modules.
struct hw_module_t {
    uint32_t tag;
    uint32_t version_major;
    uint32_t version_minor;
    const char *id;
    const char *name;
    const char *author;
    struct hw_module_methods_t *methods;
    void* dso;
    uint32_t reserved[32-7]; // Reserved for future use
};

// Common methods for all hardware modules.
struct hw_module_methods_t {
    int (*open)(const struct hw_module_t* module, const char* id, struct hw_device_t** device);
};

// Common structure for all hardware devices.
struct hw_device_t {
    uint32_t tag;
    uint32_t version;
    struct hw_module_t* module;
    int (*close)(struct hw_device_t* device);
    void* reserved[32-4]; // Reserved for future use
};

// Specific audio device structure.
typedef struct audio_hw_device {
    struct hw_device_t common; // Common device structure
    int (*write)(struct audio_hw_device* dev, const void* buffer, size_t bytes);
    void* reserved[32 - 1];
} audio_hw_device_t;

// Structure for audio module (used by HAL_MODULE_INFO_SYM).
typedef struct audio_module {
    struct hw_module_t common;
} audio_module_t;

// Macros e Constantes.
#define HARDWARE_MODULE_TAG 0x4D4F4455 // "MODU"
#define HARDWARE_DEVICE_TAG 0x44455649 // "DEVI"

#define AUDIO_DEVICE_API_VERSION_2_0 0x02000000

#define AUDIO_HARDWARE_MODULE_ID "audio"
#define AUDIO_HARDWARE_INTERFACE "primary"

// --- FIM DAS DEFINIÇÕES INJETADAS ---


// Variáveis globais para armazenar o módulo e dispositivo da HAL
static audio_module_t* gAudioModule = NULL;
static audio_hw_device_t* gAudioDevice = NULL;

// Função JNI para inicializar e chamar a HAL de áudio
extern "C" JNIEXPORT jint JNICALL
Java_com_example_myaudiohalproject_MainActivity_triggerHalAudioWrite(
        JNIEnv* env,
        jobject /* this */) {



    printf("NATIVE_LIB: triggerHalAudioWrite - INICIANDO\n");

    ALOGD("JNI: Tentando chamar triggerHalAudioWrite...");

    // 1. Tentar obter o módulo HAL de áudio
    // Para simplificar a simulação no JNI, vamos usar um ponteiro para a struct
    // HAL_MODULE_INFO_SYM definida no audio_hal.cpp.
    // Em uma implementação real, hw_get_module procuraria dinamicamente pelo módulo.
    // Como estamos contornando problemas de NDK, vamos referenciar diretamente.
    // IMPORTANTE: Isso requer que audio_hal.cpp seja compilado e linkado para que HAL_MODULE_INFO_SYM exista.
    extern struct audio_module HAL_MODULE_INFO_SYM; // Declara que HAL_MODULE_INFO_SYM existe em outro lugar

    if (gAudioModule == NULL) {
        gAudioModule = &HAL_MODULE_INFO_SYM; // Atribui diretamente a instância da HAL
        ALOGD("JNI: Módulo de áudio HAL obtido via referência direta.");
    }

    // 2. Tentar abrir o dispositivo HAL de áudio
    if (gAudioDevice == NULL) {
        // Agora, chamamos o método 'open' do módulo, que é a sua função audio_open
        int ret = gAudioModule->common.methods->open((const hw_module_t*)gAudioModule,
                                                     AUDIO_HARDWARE_INTERFACE,
                                                     (hw_device_t**)&gAudioDevice);
        if (ret != 0) {
            ALOGE("JNI: Falha ao abrir o dispositivo de áudio HAL: %d", ret);
            gAudioModule = NULL; // Reset em caso de falha
            return ret; // Retorna o código de erro
        }
        ALOGD("JNI: Dispositivo de áudio HAL aberto com sucesso.");
    }

    // 3. Chamar a função audio_write da HAL (simulada)
    unsigned char dummy_buffer[1024];
    memset(dummy_buffer, 0, sizeof(dummy_buffer)); // Preenche com zeros

    ssize_t bytes_written = gAudioDevice->write(gAudioDevice, dummy_buffer, sizeof(dummy_buffer));

    if (bytes_written >= 0) {
        ALOGD("JNI: Função audio_write da HAL chamada. Bytes processados: %zd", bytes_written);
        return 0; // Sucesso
    } else {
        ALOGE("JNI: Erro ao chamar audio_write da HAL: %zd", bytes_written);
        return (jint)bytes_written; // Retorna o erro da HAL
    }
}

// Exemplo de função JNI já presente, pode ser removida se não for usada.
// extern "C" JNIEXPORT jstring JNICALL
// Java_com_example_myaudiohalproject_MainActivity_stringFromJNI(
//     JNIEnv* env,
//     jobject /* this */) {
//     std::string hello = "Hello from C++";
//     return env->NewStringUTF(hello.c_str());
// }
