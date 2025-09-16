package com.yourcompany.julesprojem

data class Point(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val code: String = ""
)

data class Project(
    val name: String,
    val crsId: String,
    val points: MutableList<Point> = mutableListOf()
)
