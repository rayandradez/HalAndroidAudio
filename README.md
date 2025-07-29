# MyAudioHalProject

## Visão Geral do Projeto

Este projeto, **MyAudioHalProject**, demonstra a implementação de uma **Hardware Abstraction Layer (HAL) de áudio simplificada** e um aplicativo Android embarcado, o `VehicleEqualizerApp`. O objetivo principal é simular a interação entre o sistema operacional Android e componentes de hardware veiculares, como sensores e a rede CAN (Controller Area Network), abstraindo as particularidades do hardware para o software. A HAL (Hardware Abstraction Layer) do Android é uma interface que permite que o sistema operacional se comunique com os componentes de hardware, garantindo a portabilidade do sistema em diferentes dispositivos ao abstrair as especificidades do hardware para o software.

A aplicação foi desenvolvida utilizando **Kotlin** e **Java** no **Android Studio**, com o uso de **Coroutines** para simular a assincronicidade na comunicação CAN. Coroutines são threads leves que permitem escrever código assíncrono de maneira mais sequencial e legível, sendo particularmente adequadas para operações de longa duração, como requisições de rede.

## Funcionalidades

O `VehicleEqualizerApp` e sua HAL simulada foram projetados para atender aos seguintes requisitos funcionais:

*   **Otimização de Recursos**: Implementar uma técnica simples de otimização na HAL para reduzir o consumo excessivo de CPU em estado ocioso.
*   **Simulação de Sensor Veicular**: Simular a leitura de dados de diferentes sensores veiculares, incluindo velocidade (em $$km/h$$), temperatura externa (em $$°C$$) e nível de combustível (em $$%$$).
*   **Exibição e Atualização de Dados do Sensor**: Exibir a velocidade atual simulada em um `TextView` na interface do usuário e permitir a atualização manual via um botão.
*   **Simulação de Calibração de Sensor**: Incluir um método `calibrateSensor()` na classe `VehicleSensorSimulator` para simular um processo de calibração com um atraso de $$1$$ segundo.
*   **Simulação de Barramento CAN**: Simular um barramento CAN do veículo, permitindo o envio e recebimento de mensagens.
*   **Definição de Mensagem CAN**: Possibilitar a definição de mensagens CAN com um identificador (`id`) e dados (`data`).
*   **Envio de Mensagem CAN Simulado**: Permitir o envio de mensagens CAN simuladas através de um botão na interface.
*   **Exibição e Processamento de Volume CAN**: Exibir o nível de volume mestre recebido via CAN em um `TextView`. O simulador CAN processa mensagens com ID $$0x123$$ para extrair e exibir o nível de volume mestre.
*   **Integração com Equalizador de Áudio**: A funcionalidade de equalização de áudio é capaz de se integrar com um dispositivo conectado a uma rede CAN, reagindo a comandos CAN (por exemplo, ajustando o volume do áudio).
*   **Controles de Equalizador**: O aplicativo contém controles para ajustar o equalizador (Bass, Mid, Treble).
*   **Ativação/Desativação do Equalizador**: Permitir ativar e desativar o equalizador através de um `Switch`.

## Requisitos Não-Funcionais

O projeto também buscou aderir a requisitos não-funcionais importantes para a qualidade do sistema:

*   **Confiabilidade**: Comportamento previsível em cenários de sucesso e falha.
*   **Testabilidade**: Facilidade de depuração e validação através de logs.
*   **Eficiência Energética**: Otimização para minimizar o consumo de bateria.
*   **Desempenho**: Interação fluida com a UI, sem lentidão (`jank`). O simulador CAN deve liberar recursos quando não for mais necessário.
*   **Modularidade**: Design modular com classes separadas para simulação de sensores (`VehicleSensorSimulator`), mensagens CAN (`CanMessage`) e barramento CAN (`VehicleCanBusSimulator`).
*   **Clareza e Documentação**: Código e processo de desenvolvimento bem documentados.

## Arquitetura e Desenvolvimento

O projeto é estruturado em torno de classes de simulação chave e sua integração com a `MainActivity`:

### `VehicleSensorSimulator`

Esta classe Kotlin (`VehicleSensorSimulator.kt`) atua como um modelo fiel de um sensor veicular genérico. Ela gera e fornece dados simulados para parâmetros como velocidade, temperatura externa e nível de combustível.

*   **`readSensorData()`**: Retorna valores inteiros simulados dentro de intervalos específicos para cada tipo de sensor.
*   **`calibrateSensor()`**: Simula uma operação de calibração com um atraso.

A interface do usuário (`activity_main.xml`) foi modificada para exibir a velocidade simulada em um `TextView` e um botão para atualizar essa leitura, integrando-se diretamente com a lógica da `MainActivity.kt`.

### Simulação de Comunicação CAN

Para simular a rede CAN, foram criadas duas classes principais:

*   **`CanMessage`**: Uma `data class` Kotlin (`CanMessage.kt`) que padroniza a estrutura das mensagens CAN simuladas, contendo um `id` (identificador) e `data` (dados em `ByteArray`).
*   **`VehicleCanBusSimulator`**: Esta classe (`VehicleCanBusSimulator.kt`) é o coração da simulação do barramento CAN. Ela gerencia o fluxo de mensagens e notifica os componentes interessados sobre mensagens recebidas.
    *   **Gerenciamento Assíncrono**: Utiliza **Kotlin Coroutines** (`Channel`, `CoroutineScope`, `MutableSharedFlow`) para simular o envio e recebimento de mensagens de forma assíncrona, garantindo que as operações de simulação não bloqueiem a thread principal da UI.
    *   **Processamento de Mensagens**: Possui um consumidor que "ouve" continuamente as mensagens, registra-as via log e as publica para observadores. Mensagens com `ID 0x123` são processadas para extrair o nível de volume mestre.
    *   **`sendMessage()`**: Permite que outros componentes "enviem" mensagens CAN para o barramento simulado.
    *   **`stopSimulator()`**: Crucial para o gerenciamento de recursos, cancela todas as rotinas associadas e libera recursos.

A `MainActivity.kt` integra o `VehicleCanBusSimulator`, permitindo que a UI envie e receba mensagens CAN simuladas e reaja a elas, com um `TextView` dedicado para exibir o volume mestre CAN e um botão para simular o envio de mensagens de volume.

### Hardware Abstraction Layer (HAL) Conceitual

Embora a implementação física de uma HAL não seja o foco primário deste projeto simulado, o conceito de HAL é ilustrado:

*   **Ponte entre Software e Hardware Simulados**: No `VehicleCanBusSimulator`, quando uma mensagem CAN de volume é processada, o valor do volume seria, em um sistema real, repassado para uma interface dentro da HAL de áudio. A HAL conteria a lógica para converter este comando em instruções para o controlador de áudio do veículo.
*   **JNI (Java Native Interface)**: A presença conceitual de métodos JNI representa o ponto de contato entre o código Kotlin/Java do aplicativo e as bibliotecas nativas de C/C++ que compõem os drivers ou a própria HAL.
*   **Abstração para Controle do Equalizador**: Os comandos de ajuste do equalizador (graves, médios, agudos) originados na UI seriam, em um cenário de produção, traduzidos por meio de uma HAL de áudio ou drivers específicos para mensagens CAN apropriadas, enviadas ao dispositivo de áudio.

## Como Executar e Verificar

Para executar e verificar o aplicativo:

1.  **Clone o Repositório**:
    ```bash
    git clone https://github.com/rayandradez/HalAndroidAudio.git
    cd HalAndroidAudio
    ```
2.  **Abra no Android Studio**: Abra o projeto no Android Studio.
3.  **Sincronize o Gradle**: Certifique-se de que todas as dependências do Gradle sejam sincronizadas.
4.  **Execute em um Emulador/Dispositivo**: Rode o aplicativo em um emulador Android ou em um dispositivo físico.

Uma vez o aplicativo em execução:

*   **Teste da Simulação do Sensor de Velocidade**: Interaja com o botão "Atualizar Velocidade". O `TextView` de velocidade na UI deve ser atualizado com um novo valor aleatório. Verifique o **Logcat** para mensagens detalhadas do `VehicleSensorSimulator`, confirmando cada leitura.
*   **Teste da Simulação de Comunicação CAN**: Clique no botão "Enviar Volume CAN (Simulado)". O `TextView` "Volume CAN" na UI deve ser atualizado dinamicamente com um valor de volume aleatório. No **Logcat**, você observará mensagens detalhadas do `VehicleCanBusSimulator` registrando tanto o envio quanto o recebimento das mensagens CAN (com IDs e dados em hexadecimal).

## Tecnologias Utilizadas

*   **Kotlin**
*   **Java**
*   **Android Studio**
*   **Kotlin Coroutines**
*   **Gradle**

## Conclusão

Este projeto demonstrou com sucesso a criação de uma plataforma simulada para explorar a complexa interação entre um aplicativo Android e sistemas de hardware veiculares. A utilização de abstrações eficazes como `VehicleSensorSimulator` e `VehicleCanBusSimulator`, combinada com o poder das Kotlin Coroutines para simular a comunicação assíncrona, permitiu a validação de fluxos de dados e a compreensão dos conceitos fundamentais de uma Hardware Abstraction Layer (HAL) e sua interação via JNI, preparando o terreno para futuras implementações em ambientes de hardware veicular reais.
