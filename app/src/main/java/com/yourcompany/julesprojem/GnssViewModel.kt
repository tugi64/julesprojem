package com.yourcompany.julesprojem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GnssViewModel(
    private val bluetoothService: BluetoothService,
    private val ntripClient: NtripClient
) : ViewModel() {

    private val nmeaParser = NmeaParser()

    private val _ggaData = MutableStateFlow<GgaData?>(null)
    val ggaData = _ggaData.asStateFlow()

    init {
        // Start simulating a GNSS data stream
        viewModelScope.launch {
            while (isActive) {
                // A sample $GPGGA sentence for Istanbul
                val sampleNmea = "\$GPGGA,123519,4100.8385,N,02900.9329,E,1,08,0.9,545.4,M,46.9,M,,*47"
                val parsedData = nmeaParser.parseGga(sampleNmea)
                if (parsedData != null) {
                    // Slightly vary the position to simulate movement
                    _ggaData.value = parsedData.copy(
                        latitude = parsedData.latitude + (Math.random() - 0.5) * 0.00001,
                        longitude = parsedData.longitude + (Math.random() - 0.5) * 0.00001
                    )
                }
                delay(1000) // Emit new data every second
            }
        }
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
