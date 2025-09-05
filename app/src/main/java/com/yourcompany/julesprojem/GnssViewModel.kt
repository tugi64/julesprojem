package com.yourcompany.julesprojem

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GnssViewModel(
    private val bluetoothService: BluetoothService,
    private val ntripClient: NtripClient
    ) : ViewModel(), GnssUiState {

    override var selectedManufacturer by mutableStateOf("South")
    override var selectedModel by mutableStateOf("ALPS2")
    override var selectedBluetoothDevice by mutableStateOf("")
    override var rtkMode by mutableStateOf("RTK")
    override var corsHost by mutableStateOf("ntrip.example.com")
    override var corsPort by mutableStateOf("2101")
    override var corsUser by mutableStateOf("user")
    override var corsPass by mutableStateOf("pass")
    override var connectionStatus by mutableStateOf("Bağlı Değil")

    override val manufacturers = listOf("South", "Trimble", "Topcon", "Hi-Target", "Sokkia", "Leica")
    override val models = listOf("ALPS2", "G1", "G6")
    private val _bluetoothDevices = mutableStateOf<List<String>>(emptyList())
    override val bluetoothDevices: State<List<String>> = _bluetoothDevices
    override val rtkModes = listOf("RTK", "PPK")

    init {
        scanForDevices()
    }

    fun scanForDevices() {
        val pairedDevices = bluetoothService.getPairedDevices()
        _bluetoothDevices.value = pairedDevices.map { it.name ?: "Bilinmeyen Cihaz" }
        if (_bluetoothDevices.value.isNotEmpty()) {
            selectedBluetoothDevice = _bluetoothDevices.value[0]
        }
    }

    fun onConnectClicked() {
        viewModelScope.launch {
            connectionStatus = "Bluetooth'a bağlanılıyor..."
            // TODO: Replace with actual bluetoothService.connect(selectedBluetoothDevice)
            // For now, we simulate a successful connection.

            connectionStatus = "CORS'a bağlanılıyor..."
            try {
                ntripClient.connect(corsHost, corsPort.toInt())
                // TODO: Handle NTRIP authentication and data stream
                connectionStatus = "Bağlandı ve Düzeltme Alınıyor"
            } catch (e: Exception) {
                connectionStatus = "CORS Bağlantı Hatası"
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ntripClient.disconnect()
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
