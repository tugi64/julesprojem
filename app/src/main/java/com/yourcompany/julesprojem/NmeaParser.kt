// NmeaParser.kt
package com.yourcompany.julesprojem

object NmeaParser {
    fun parseGGA(nmeaSentence: String): GgaData? {
        if (!nmeaSentence.startsWith("\$GPGGA")) {
            return null
        }

        val parts = nmeaSentence.split(",")
        if (parts.size < 15) {
            return null
        }

        return try {
            val latitude = convertToDecimalDegrees(parts[2], parts[3])
            val longitude = convertToDecimalDegrees(parts[4], parts[5])
            val altitude = parts[9].toDoubleOrNull() ?: 0.0
            val satelliteCount = parts[7].toIntOrNull() ?: 0
            val hdop = parts[8].toDoubleOrNull() ?: 0.0
            val fixQuality = when (parts[6].toIntOrNull()) {
                0 -> "No fix"
                1 -> "GPS fix"
                2 -> "DGPS fix"
                3 -> "PPS fix"
                4 -> "RTK"
                5 -> "Float RTK"
                6 -> "Estimated"
                7 -> "Manual"
                8 -> "Simulation"
                else -> "Unknown"
            }

            GgaData(latitude, longitude, altitude, satelliteCount, hdop, fixQuality)
        } catch (e: Exception) {
            null
        }
    }

    private fun convertToDecimalDegrees(coord: String, direction: String): Double {
        if (coord.isEmpty()) return 0.0

        val degrees = coord.substring(0, 2).toDouble()
        val minutes = coord.substring(2).toDouble()
        var decimal = degrees + minutes / 60.0

        if (direction == "S" || direction == "W") {
            decimal = -decimal
        }

        return decimal
    }
}