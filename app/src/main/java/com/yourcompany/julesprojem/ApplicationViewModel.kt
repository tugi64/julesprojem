package com.yourcompany.julesprojem

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

data class StakeoutGuidance(
    val distance: Double,
    val bearing: Double
)

class ApplicationViewModel : ViewModel() {

    val stakeoutTarget = ProjectRepository.stakeoutTarget

    private val _guidance = MutableStateFlow<StakeoutGuidance?>(null)
    val guidance = _guidance.asStateFlow()

    fun updateLocation(ggaData: GgaData?) {
        val target = stakeoutTarget.value
        if (ggaData == null || target == null) {
            _guidance.value = null
            return
        }

        val distance = calculateDistance(
            lat1 = ggaData.latitude,
            lon1 = ggaData.longitude,
            lat2 = target.latitude,
            lon2 = target.longitude
        )

        val bearing = calculateBearing(
            lat1 = ggaData.latitude,
            lon1 = ggaData.longitude,
            lat2 = target.latitude,
            lon2 = target.longitude
        )

        _guidance.value = StakeoutGuidance(distance, bearing)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371e3 // metres
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaPhi = Math.toRadians(lat2 - lat1)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return r * c // in metres
    }

    private fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val phi1 = Math.toRadians(lat1)
        val lambda1 = Math.toRadians(lon1)
        val phi2 = Math.toRadians(lat2)
        val lambda2 = Math.toRadians(lon2)

        val y = sin(lambda2 - lambda1) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(lambda2 - lambda1)
        val theta = atan2(y, x)

        return (Math.toDegrees(theta) + 360) % 360 // in degrees
    }
}