package com.yourcompany.julesprojem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.InputStream

class GnssViewModel(
    private val bluetoothService: BluetoothService,
    private val ntripClient: NtripClient
) : ViewModel() {

    private val nmeaParser = NmeaParser()
    private var dataStreamJob: Job? = null

    private val _ggaData = MutableStateFlow<GgaData?>(null)
    val ggaData = _ggaData.asStateFlow()

    fun startDataStream(inputStream: InputStream) {
        // Cancel any existing stream
        stopDataStream()
        dataStreamJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                inputStream.bufferedReader().use { reader ->
                    while (isActive) {
                        val line = reader.readLine()
                        if (line != null) {
                            nmeaParser.parseGga(line)?.let {
                                _ggaData.value = it
                            }
                        } else {
                            // End of stream
                            break
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exceptions, e.g., disconnection
                e.printStackTrace()
            } finally {
                 // Optionally update a status a status here
            }
        }
    }

    fun stopDataStream() {
        dataStreamJob?.cancel()
        dataStreamJob = null
        _ggaData.value = null // Clear data on disconnect
    }

    fun savePoint() {
        val currentGga = ggaData.value ?: return
        val activeProject = ProjectRepository.activeProject.value ?: return

        val newPoint = Point(
            name = "Point-${activeProject.points.size + 1}",
            latitude = currentGga.latitude,
            longitude = currentGga.longitude,
            altitude = currentGga.altitude
        )

        activeProject.points.add(newPoint)
        ProjectRepository.saveProject(activeProject)
    }

    override fun onCleared() {
        super.onCleared()
        stopDataStream()
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
