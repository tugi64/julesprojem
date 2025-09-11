package com.yourcompany.julesprojem.fileio

import com.yourcompany.julesprojem.Point
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import org.locationtech.proj4j.ProjCoordinate

object CsvParser {

    fun parse(fileContent: String, crsId: String): ImportResult {
        val points = mutableListOf<Point>()
        val lines = fileContent.lines().filter { it.isNotBlank() }

        // Get the inverse transformation from the project CRS to WGS84
        val transform = CoordinateSystemManager.getInverseTransform(crsId)
            ?: return ImportResult.Error("Could not create inverse transformation for CRS: $crsId")

        for ((index, line) in lines.withIndex()) {
            val fields = line.split(',').map { it.trim() }
            if (fields.size < 4) {
                return ImportResult.Error("Error on line ${index + 1}: Not enough columns. Expected at least Name,X,Y,Z.")
            }

            try {
                val name = fields[0]
                val x = fields[1].toDouble()
                val y = fields[2].toDouble()
                val z = fields[3].toDouble()
                val code = fields.getOrNull(4) ?: ""

                // Perform inverse transformation
                val sourceCoord = ProjCoordinate(x, y, z)
                val targetCoord = ProjCoordinate()
                transform.transform(sourceCoord, targetCoord)

                if (targetCoord.x.isNaN() || targetCoord.y.isNaN()) {
                    return ImportResult.Error("Error on line ${index + 1}: Coordinate transformation failed.")
                }

                points.add(
                    Point(
                        name = name,
                        // Note: Proj4J returns Lon, Lat
                        longitude = targetCoord.x,
                        latitude = targetCoord.y,
                        altitude = targetCoord.z,
                        code = code
                    )
                )
            } catch (e: NumberFormatException) {
                return ImportResult.Error("Error on line ${index + 1}: Invalid number format.")
            } catch (e: Exception) {
                return ImportResult.Error("An unexpected error occurred on line ${index + 1}: ${e.message}")
            }
        }
        return ImportResult.Success(points)
    }
}
