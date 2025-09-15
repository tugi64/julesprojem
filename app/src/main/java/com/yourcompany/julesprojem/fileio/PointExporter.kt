package com.yourcompany.julesprojem.fileio

import com.yourcompany.julesprojem.Point
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import java.util.Locale

object PointExporter {

    fun exportAsCsv(points: List<Point>, crsId: String): String? {
        val header = "Name,X,Y,Z,Code"
        val rows = mutableListOf<String>()
        rows.add(header)

        for (point in points) {
            val projectedPoint = CoordinateSystemManager.transformToProjected(
                lat = point.latitude,
                lon = point.longitude,
                alt = point.altitude,
                targetCrsId = crsId
            )

            if (projectedPoint != null) {
                val row = String.format(
                    Locale.US,
                    "%s,%.3f,%.3f,%.3f,%s",
                    point.name,
                    projectedPoint.x,
                    projectedPoint.y,
                    projectedPoint.z,
                    point.code
                )
                rows.add(row)
            } else {
                println("Warning: Could not transform point ${point.name} to CRS $crsId. It will not be exported.")
            }
        }

        if (rows.size <= 1) {
            return null
        }

        return rows.joinToString(separator = "\n")
    }
}
