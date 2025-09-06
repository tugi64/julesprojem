package com.yourcompany.julesprojem.coords

import org.locationtech.proj4j.*

data class CrsData(
    val id: String, // e.g., "EPSG:4326" or a custom ID
    val name: String // e.g., "WGS 84"
)

data class ProjectedPoint(
    val x: Double,
    val y: Double,
    val z: Double
)

object CoordinateSystemManager {

    private val crsFactory = CRSFactory()
    private val ctFactory = CoordinateTransformFactory()

    // Define source CRS (always WGS84 for GNSS)
    private val WGS84: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")

    val predefinedSystems = listOf(
        CrsData("EPSG:4326", "WGS 84"),
        CrsData("ITRF96_TM30", "ITRF96 / TM30")
    )

    private val crsCache = mutableMapOf<String, CoordinateReferenceSystem>()

    init {
        // Pre-cache WGS84
        crsCache["EPSG:4326"] = WGS84

        // Define and cache the custom proj4 string for ITRF96 / TM30
        val itrf96tm30Params = "+proj=tmerc +lat_0=0 +lon_0=30 +k=1 +x_0=500000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
        crsCache["ITRF96_TM30"] = crsFactory.createFromParameters("ITRF96 / TM30", itrf96tm30Params)
    }

    private fun getCrs(id: String): CoordinateReferenceSystem? {
        if (crsCache.containsKey(id)) {
            return crsCache[id]
        }
        // This basic implementation only supports pre-cached systems.
        // It can be expanded to create EPSG codes on the fly if needed.
        return null
    }

    fun transformToProjected(lat: Double, lon: Double, alt: Double, targetCrsId: String): ProjectedPoint? {
        val targetCrs = getCrs(targetCrsId) ?: return null

        val transform: CoordinateTransform = ctFactory.createTransform(WGS84, targetCrs)

        val sourceCoord = ProjCoordinate(lon, lat, alt)
        val targetCoord = ProjCoordinate()

        return try {
            transform.transform(sourceCoord, targetCoord)
            if (targetCoord.x.isNaN() || targetCoord.y.isNaN()) {
                null
            } else {
                ProjectedPoint(targetCoord.x, targetCoord.y, targetCoord.z)
            }
        } catch (e: Proj4jException) {
            // In a real app, log this exception
            e.printStackTrace()
            null
        }
    }

    fun findCrsById(id: String): CrsData? {
        return predefinedSystems.find { it.id == id }
    }
}
