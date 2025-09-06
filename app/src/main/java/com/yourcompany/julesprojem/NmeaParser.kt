package com.yourcompany.julesprojem

data class GgaData(
    val time: String,
    val latitude: Double,
    val longitude: Double,
    val fixQuality: Int,
    val satelliteCount: Int,
    val hdop: Double,
    val altitude: Double
)

class NmeaParser {

    fun parseGga(sentence: String): GgaData? {
        if (!sentence.startsWith("\$GPGGA")) {
            return null
        }

        val parts = sentence.substring(1).split("*")
        if (parts.size != 2) {
            return null // No checksum
        }

        val data = parts[0]
        val checksum = parts[1]

        var calculatedChecksum = 0
        for (char in data) {
            calculatedChecksum = calculatedChecksum xor char.code
        }
        if (checksum.toIntOrNull(16) != calculatedChecksum) {
             return null // Invalid checksum
        }

        val fields = data.split(",")
        if (fields.size < 11) {
            return null // Not enough fields
        }

        try {
            val time = fields[1]
            val lat = ddmToDecimal(fields[2], fields[3])
            val lon = ddmToDecimal(fields[4], fields[5])
            val fixQuality = fields[6].toIntOrNull() ?: 0
            val satelliteCount = fields[7].toIntOrNull() ?: 0
            val hdop = fields[8].toDoubleOrNull() ?: 99.9
            val altitude = fields[9].toDoubleOrNull() ?: 0.0

            if (lat != null && lon != null) {
                return GgaData(time, lat, lon, fixQuality, satelliteCount, hdop, altitude)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun ddmToDecimal(ddm: String, direction: String): Double? {
        if (ddm.isBlank() || !ddm.contains(".")) return null
        try {
            val pointIndex = ddm.indexOf('.')
            val degPart = ddm.substring(0, pointIndex - 2)
            val minPart = ddm.substring(pointIndex - 2)

            val degrees = degPart.toDouble()
            val minutes = minPart.toDouble()

            var decimal = degrees + minutes / 60.0
            if (direction == "S" || direction == "W") {
                decimal *= -1.0
            }
            return decimal
        } catch (e: NumberFormatException) {
            return null
        }
    }
}
