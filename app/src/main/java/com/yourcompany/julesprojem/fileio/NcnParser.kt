package com.yourcompany.julesprojem.fileio

import com.yourcompany.julesprojem.Point
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import org.locationtech.proj4j.ProjCoordinate

object NcnParser {

    // Assumes the format is space-delimited: Name Y X Z Code
    fun parse(fileContent: String, crsId: String): ImportResult {
        val points = mutableListOf<Point>()
        val lines = fileContent.lines().filter { it.isNotBlank() }

        val transform = CoordinateSystemManager.getInverseTransform(crsId)
            ?: return ImportResult.Error("Could not create inverse transformation for CRS: $crsId")

        for ((index, line) in lines.withIndex()) {
            // Split by one or more spaces
            val fields = line.trim().split("\\s+".toRegex())
            if (fields.size < 4) {
                return ImportResult.Error("Error on NCN line ${index + 1}: Not enough columns. Expected at least Name,Y,X,Z.")
            }

            try {
                val name = fields[0]
                // Note the order: Y is Northing (projected y), X is Easting (projected x)
                val y = fields[1].toDouble()
                val x = fields[2].toDouble()
                val z = fields[3].toDouble()
                val code = fields.getOrNull(4) ?: ""

                val sourceCoord = ProjCoordinate(x, y, z)
                val targetCoord = ProjCoordinate()
                transform.transform(sourceCoord, targetCoord)

                if (targetCoord.x.isNaN() || targetCoord.y.isNaN()) {
                    return ImportResult.Error("Error on NCN line ${index + 1}: Coordinate transformation failed.")
                }

                points.add(
                    Point(
                        name = name,
                        longitude = targetCoord.x,
                        latitude = targetCoord.y,
                        altitude = targetCoord.z,
                        code = code
                    )
                )
            } catch (e: NumberFormatException) {
                return ImportResult.Error("Error on NCN line ${index + 1}: Invalid number format.")
            } catch (e: Exception) {
                return ImportResult.Error("An unexpected error occurred on NCN line ${index + 1}: ${e.message}")
            }
        }
        return ImportResult.Success(points)
    }
}
