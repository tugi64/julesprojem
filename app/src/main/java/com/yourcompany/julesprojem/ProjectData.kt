package com.yourcompany.julesprojem

data class Point(
    val name: String,
    val northing: Double,
    val easting: Double,
    val elevation: Double,
    val code: String = ""
)

data class CoordinateSystem(
    val name: String,
    val ellipsoid: String,
    val projection: String,
    val centralMeridian: Double? = null
)

data class Project(
    val name: String,
    val coordinateSystem: CoordinateSystem,
    val points: MutableList<Point> = mutableListOf()
)
