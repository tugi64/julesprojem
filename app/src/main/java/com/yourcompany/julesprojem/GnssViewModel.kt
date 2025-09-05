package com.yourcompany.julesprojem

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission") // Permissions are handled in MainActivity
class GnssViewModel(
    private val bluetoothService: BluetoothService,
    private val ntripClient: NtripClient
    ) : ViewModel(), GnssUiState {

    private val nmeaParser = NmeaParser()

    override var selectedManufacturer by mutableStateOf("South")
    override var selectedModel by mutableStateOf("ALPS2")
    override var selectedBluetoothDevice by mutableStateOf("")
    override var rtkMode by mutableStateOf("RTK")
    override var corsHost by mutableStateOf("ntrip.example.com")
    override var corsPort by mutableStateOf("2101")
    override var corsUser by mutableStateOf("user")
    override var corsPass by mutableStateOf("pass")
    override var connectionStatus by mutableStateOf("Bağlı Değil")
    override var isScanning by mutableStateOf(false)
    override var ggaData by mutableStateOf<GgaData?>(null)


    override val manufacturers = listOf("South", "Trimble", "Topcon", "Hi-Target", "Sokkia", "Leica")
    override val models = listOf("ALPS2", "G1", "G6")
    private val _bluetoothDevices = mutableStateOf<List<Pair<String, String>>>(emptyList())
    override val bluetoothDevices: State<List<Pair<String, String>>> = _bluetoothDevices
    override val rtkModes = listOf("RTK", "PPK")

    init {
        getPairedDevices()
    }

    private fun getPairedDevices() {
        val pairedDevices = bluetoothService.getPairedDevices()
        _bluetoothDevices.value = pairedDevices.map { (it.name ?: "Bilinmeyen Cihaz") to it.address }
        if (_bluetoothDevices.value.isNotEmpty()) {
            selectedBluetoothDevice = _bluetoothDevices.value[0].second
        }
    }

    fun scanForDevices() {
        viewModelScope.launch {
            isScanning = true
            val currentDevices = mutableMapOf<String, String>()
            _bluetoothDevices.value.forEach { currentDevices[it.second] = it.first }

            bluetoothService.getPairedDevices().forEach { device ->
                currentDevices[device.address] = device.name ?: "Bilinmeyen Cihaz"
            }
            _bluetoothDevices.value = currentDevices.map { it.value to it.key }

            bluetoothService.startDiscovery { device ->
                val deviceName = device.name
                val deviceAddress = device.address
                if (!deviceName.isNullOrBlank() && !currentDevices.containsKey(deviceAddress)) {
                    currentDevices[deviceAddress] = deviceName
                    _bluetoothDevices.value = currentDevices.map { it.value to it.key }
                }
            }

            delay(15000)
            isScanning = false
            bluetoothService.cancelDiscovery()
        }
    }

    fun onConnectClicked() {
        viewModelScope.launch {
            connectionStatus = "Bluetooth'a bağlanılıyor..."
            val result = withContext(Dispatchers.IO) {
                bluetoothService.connect(selectedBluetoothDevice)
            }

            result.onSuccess { inputStream ->
                connectionStatus = "Bağlandı. Veri bekleniyor..."
                viewModelScope.launch(Dispatchers.IO) {
                    val reader = inputStream.bufferedReader()
                    while(isActive) {
                        try {
                            val line = reader.readLine()
                            if (line != null) {
                                val parsedData = nmeaParser.parseGga(line)
                                if (parsedData != null) {
                                    withContext(Dispatchers.Main) {
                                        ggaData = parsedData
                                        connectionStatus = "Veri alınıyor..."
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                connectionStatus = "Veri okuma hatası: ${e.message}"
                            }
                            break
                        }
                    }
                }
            }.onFailure {
                connectionStatus = "Bluetooth bağlantı hatası"
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ntripClient.disconnect()
        bluetoothService.disconnect()
    }

    @Suppress("UNCHECKED_CAST")
    class GnssViewModelFactory(
        private val bluetoothService: BluetoothService,
        private val ntripClient: NtripClient
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GnssViewModel::class.java)) {
                return GnssViewModel(bluetoothService, ntripClient) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
