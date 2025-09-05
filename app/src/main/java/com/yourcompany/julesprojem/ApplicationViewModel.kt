package com.yourcompany.julesprojem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

data class StakeoutGuidance(
    val distance: Double,
    val bearing: Double
)

class ApplicationViewModel : ViewModel() {

    val stakeoutTarget: StateFlow<Point?> = ProjectRepository.stakeoutTarget

    private val _guidance = MutableStateFlow<StakeoutGuidance?>(null)
    val guidance: StateFlow<StakeoutGuidance?> = _guidance

    fun updateLocation(ggaData: GgaData?) {
        val target = stakeoutTarget.value
        if (ggaData == null || target == null) {
            _guidance.value = null
            return
        }

        val distance = CalculationUtils.calculateDistance(
            lat1 = ggaData.latitude,
            lon1 = ggaData.longitude,
            lat2 = target.northing, // Assuming N is lat, E is lon
            lon2 = target.easting
        )

        val bearing = CalculationUtils.calculateBearing(
            lat1 = ggaData.latitude,
            lon1 = ggaData.longitude,
            lat2 = target.northing,
            lon2 = target.easting
        )

        _guidance.value = StakeoutGuidance(distance, bearing)
    }
}
