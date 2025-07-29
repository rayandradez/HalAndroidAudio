package com.example.myaudiohalproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Button
import android.util.Log
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.example.myaudiohalproject.com.example.myaudiohalproject.VehicleCanBusSimulator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel
import com.example.myaudiohalproject.com.example.myaudiohalproject.CanMessage


class MainActivity : AppCompatActivity() {

    private lateinit var equalizerSwitch: Switch
    private lateinit var bassSeekBar: SeekBar
    private lateinit var midSeekBar: SeekBar
    private lateinit var trebleSeekBar: SeekBar
    private lateinit var speedLabel: TextView
    private lateinit var readSpeedButton: Button
    private lateinit var canVolumeLabel: TextView // Novo TextView para volume CAN
    private lateinit var sendCanVolumeButton: Button // Novo Botão para volume CAN
    private lateinit var nativeStatusTextView: TextView
    private val TAG = "VehicleEqualizerApp"
    private var equalizerService: IEqualizerService? = null
    private val vehicleSensorSimulator = VehicleSensorSimulator("Velocidade")
    private val vehicleCanBusSimulator = VehicleCanBusSimulator() // Instância do simulador CAN
    private val activityScope = CoroutineScope(Dispatchers.Main) // Escopo para corrotinas da Activity



    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service:
        IBinder?) {
            equalizerService = IEqualizerService.Stub.asInterface(service)
            Log.i(TAG, "Conectado ao EqualizerService.")
            equalizerService?.setEqualizerEnabled(equalizerSwitch.isChecked)
            equalizerService?.setBassLevel(bassSeekBar.progress)
            equalizerService?.setMidLevel(midSeekBar.progress)
            equalizerService?.setTrebleLevel(trebleSeekBar.progress)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            equalizerService = null
            Log.w(TAG, "Desconectado do EqualizerService.")
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        equalizerSwitch = findViewById(R.id.equalizerSwitch)
        bassSeekBar = findViewById(R.id.bassSeekBar)
        midSeekBar = findViewById(R.id.midSeekBar)
        trebleSeekBar = findViewById(R.id.trebleSeekBar)
        speedLabel = findViewById(R.id.speedLabel)
        readSpeedButton = findViewById(R.id.readSpeedButton)
        canVolumeLabel = findViewById(R.id.canVolumeLabel) // Inicializao TextView CAN
        sendCanVolumeButton = findViewById(R.id.sendCanVolumeButton) // Inicializa o Botão CAN
        nativeStatusTextView = findViewById(R.id.nativeStatusTextView)

        nativeStatusTextView.text = stringFromJNI()

        setEqualizerControlsEnabled(equalizerSwitch.isChecked)

        equalizerSwitch.setOnCheckedChangeListener { _, isChecked ->
            setEqualizerControlsEnabled(isChecked)
            equalizerService?.setEqualizerEnabled(isChecked)
            setEqualizerEnabledNative(isChecked)
        }

        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                when (seekBar?.id) {
                    R.id.bassSeekBar -> {
                        equalizerService?.setBassLevel(seekBar.progress)
                        setBassLevelNative(seekBar.progress)
                    }
                    R.id.midSeekBar -> {
                        equalizerService?.setMidLevel(seekBar.progress)
                        setMidLevelNative(seekBar.progress)
                    }
                    R.id.trebleSeekBar -> {
                        equalizerService?.setTrebleLevel(seekBar.progress)
                        setTrebleLevelNative(seekBar.progress)
                    }
                }
            }
        }
        bassSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        midSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        trebleSeekBar.setOnSeekBarChangeListener(seekBarChangeListener)
        readSpeedButton.setOnClickListener {
            val currentSpeed = vehicleSensorSimulator.readSensorData()
            speedLabel.text = "Velocidade Atual: $currentSpeed km/h"
            Log.d(TAG, "Velocidade lida: $currentSpeed km/h")
        }

        // Listener para o botão de envio de volume CAN
        sendCanVolumeButton.setOnClickListener { // Simula o envio de uma mensagem CAN de volume (ID 0x123, valor aleatório 0-100)
            val randomVolume = (0..100).random()
            val message = CanMessage (id = 0x123, data = byteArrayOf(randomVolume.toByte()))
            vehicleCanBusSimulator.sendMessage(message)
        }

        // Coleta mensagens CAN recebidas e atualiza a UI
        activityScope.launch {
            vehicleCanBusSimulator.canMessageFlow.collect { message ->
                if (message.id == 0x123 && message.data.isNotEmpty()) {
                    val volume = message.data[0].toInt() and 0xFF
                    canVolumeLabel.text = "Volume CAN: $volume"
                    // Em um cenário real, você poderia usar este volume para ajustar o áudio
                    // equalizerService?.setMasterVolume(volume) //Exemplo de chamada ao serviço
                }
            }
        }
    }




    override fun onStart() {
        super.onStart()
        val intent = Intent(this, EqualizerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


    override fun onStop() {
        super.onStop()
        if (equalizerService != null) {
            unbindService(serviceConnection)
            equalizerService = null
            Log.i(TAG, "Desvinculado do EqualizerService.")
        }
        // Importante: parar o simulador CAN quando a atividade for destruída ou não for mais necessária
        vehicleCanBusSimulator.stopSimulator()
        activityScope.cancel() // Cancela as corrotinas do escopo da atividade
    }



    private fun setEqualizerControlsEnabled(enabled: Boolean) {
        bassSeekBar.isEnabled = enabled
        midSeekBar.isEnabled = enabled
        trebleSeekBar.isEnabled = enabled
    }

    external fun stringFromJNI(): String
    external fun setEqualizerEnabledNative(enabled: Boolean)
    external fun setBassLevelNative(level: Int)
    external fun setMidLevelNative(level: Int)
    external fun setTrebleLevelNative(level: Int)

    companion object {
        init {
            System.loadLibrary("vehicleequalizernative")
        }
    }
}
