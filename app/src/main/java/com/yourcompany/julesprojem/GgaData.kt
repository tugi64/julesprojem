// GgaData.kt
package com.yourcompany.julesprojem

data class GgaData(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val satelliteCount: Int,
    val hdop: Double,
    val fixQuality: String
)