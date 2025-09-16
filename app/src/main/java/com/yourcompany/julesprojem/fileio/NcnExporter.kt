package com.yourcompany.julesprojem.fileio

import com.yourcompany.julesprojem.Point
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import java.util.Locale

object NcnExporter {

    // Exports in the format: Name Y X Z Code (space-delimited)
    fun exportAsNcn(points: List<Point>, crsId: String): String? {
        val rows = mutableListOf<String>()

        for (point in points) {
            val projectedPoint = CoordinateSystemManager.transformToProjected(
                lat = point.latitude,
                lon = point.longitude,
                alt = point.altitude,
                targetCrsId = crsId
            )

            if (projectedPoint != null) {
                // Note the order: Y (Northing), X (Easting)
                val row = String.format(
                    Locale.US,
                    "%s %.3f %.3f %.3f %s",
                    point.name,
                    projectedPoint.y,
                    projectedPoint.x,
                    projectedPoint.z,
                    point.code
                ).trim() // Use trim to remove trailing space if code is empty
                rows.add(row)
            } else {
                println("Warning: Could not transform point ${point.name} to CRS $crsId for NCN export.")
            }
        }

        if (rows.isEmpty()) {
            return null
        }

        return rows.joinToString(separator = "\n")
    }
}
