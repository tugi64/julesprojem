package com.yourcompany.julesprojem

/**
 * Represents a single point measured by the GNSS receiver.
 * The base coordinates are always stored in WGS84 (latitude, longitude).
 * Projected coordinates are calculated on-the-fly when needed.
 */
data class Point(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val code: String = ""
)

/**
 * Represents a survey project.
 * It contains the project's name and its chosen coordinate reference system,
 * identified by a unique ID string (e.g., "EPSG:4326").
 * The list of points is stored within the project.
 */
data class Project(
    val name: String,
    val crsId: String, // ID referencing a system in CoordinateSystemManager
    val points: MutableList<Point> = mutableListOf()
)
