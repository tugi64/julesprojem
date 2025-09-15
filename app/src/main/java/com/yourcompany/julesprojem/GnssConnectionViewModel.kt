package com.yourcompany.julesprojem

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream

@SuppressLint("MissingPermission")
class GnssConnectionViewModel(
    private val bluetoothService: BluetoothService,
    private val gnssViewModel: GnssViewModel
) : ViewModel() {

    private val _pairedDevices = mutableStateListOf<Pair<String, String>>()
    val pairedDevices: List<Pair<String, String>> = _pairedDevices

    private val _scannedDevices = mutableStateListOf<Pair<String, String>>()
    val scannedDevices: List<Pair<String, String>> = _scannedDevices

    private val _connectionStatus = MutableStateFlow("Not Connected")
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()


    init {
        getPairedDevices()
    }

    private fun getPairedDevices() {
        _pairedDevices.clear()
        bluetoothService.getPairedDevices().forEach { device ->
            _pairedDevices.add((device.name ?: "Unknown Device") to device.address)
        }
    }

    fun scanForDevices() {
        viewModelScope.launch {
            _isScanning.value = true
            _scannedDevices.clear()
            bluetoothService.startDiscovery { device ->
                if (_scannedDevices.none { it.second == device.address } && _pairedDevices.none { it.second == device.address }) {
                    _scannedDevices.add((device.name ?: "Unknown Device") to device.address)
                }
            }
        }
    }

    fun stopScanning() {
        _isScanning.value = false
        bluetoothService.cancelDiscovery()
    }

    fun connectToDevice(deviceAddress: String) {
        viewModelScope.launch {
            _isConnecting.value = true
            _connectionStatus.value = "Connecting..."
            val result = bluetoothService.connect(deviceAddress)
            result.onSuccess { inputStream ->
                _connectionStatus.value = "Connected. Starting data stream..."
                gnssViewModel.startDataStream(inputStream)
            }.onFailure { error ->
                _connectionStatus.value = "Connection failed: ${error.message}"
            }
            _isConnecting.value = false
        }
    }

    fun disconnect() {
        bluetoothService.disconnect()
        gnssViewModel.stopDataStream()
        _connectionStatus.value = "Not Connected"
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }
}
