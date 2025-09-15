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
    private val WGS84: CoordinateReferenceSystem = crsFactory.createFromName("EPSG:4326")

    // A more comprehensive list of common systems
    val predefinedSystems = listOf(
        CrsData("EPSG:4326", "WGS 84"),
        CrsData("ITRF96_TM30", "ITRF96 / TM30 (Custom)"),
        // Turkish Systems based on ITRF96
        CrsData("EPSG:5253", "ITRF96 / 3-degree Gauss-Kruger zone 9 (TM27)"),
        CrsData("EPSG:5254", "ITRF96 / 3-degree Gauss-Kruger zone 10 (TM30)"),
        CrsData("EPSG:5255", "ITRF96 / 3-degree Gauss-Kruger zone 11 (TM33)"),
        CrsData("EPSG:5256", "ITRF96 / 3-degree Gauss-Kruger zone 12 (TM36)"),
        CrsData("EPSG:5257", "ITRF96 / 3-degree Gauss-Kruger zone 13 (TM39)"),
        CrsData("EPSG:5258", "ITRF96 / 3-degree Gauss-Kruger zone 14 (TM42)"),
        CrsData("EPSG:5259", "ITRF96 / 3-degree Gauss-Kruger zone 15 (TM45)"),
        // Common Global UTM Zones (WGS84)
        CrsData("EPSG:32635", "WGS 84 / UTM zone 35N"),
        CrsData("EPSG:32636", "WGS 84 / UTM zone 36N"),
        CrsData("EPSG:32637", "WGS 84 / UTM zone 37N"),
    )

    private val crsCache = mutableMapOf<String, CoordinateReferenceSystem>()

    init {
        // Pre-cache WGS84 and the custom projection
        crsCache["EPSG:4326"] = WGS84
        val itrf96tm30Params = "+proj=tmerc +lat_0=0 +lon_0=30 +k=1 +x_0=500000 +y_0=0 +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +units=m +no_defs"
        crsCache["ITRF96_TM30"] = crsFactory.createFromParameters("ITRF96 / TM30 (Custom)", itrf96tm30Params)
    }

    private fun getCrsById(id: String): CoordinateReferenceSystem? {
        if (crsCache.containsKey(id)) {
            return crsCache[id]
        }
        // For on-the-fly creation from EPSG codes
        if (id.startsWith("EPSG:")) {
            return try {
                val crs = crsFactory.createFromName(id)
                crsCache[id] = crs
                crs
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        return null // Return null for non-cached custom IDs that weren't in the init block
    }

    // Transform from WGS84 to a Projected CRS
    fun getForwardTransform(targetCrsId: String): CoordinateTransform? {
        val targetCrs = getCrsById(targetCrsId) ?: return null
        return ctFactory.createTransform(WGS84, targetCrs)
    }

    // Transform from a Projected CRS to WGS84
    fun getInverseTransform(sourceCrsId: String): CoordinateTransform? {
        val sourceCrs = getCrsById(sourceCrsId) ?: return null
        return ctFactory.createTransform(sourceCrs, WGS84)
    }

    fun transformToProjected(lat: Double, lon: Double, alt: Double, targetCrsId: String): ProjectedPoint? {
        val transform = getForwardTransform(targetCrsId) ?: return null
        val sourceCoord = ProjCoordinate(lon, lat, alt)
        val targetCoord = ProjCoordinate()

        return try {
            transform.transform(sourceCoord, targetCoord)
            if (targetCoord.x.isNaN() || targetCoord.y.isNaN()) null
            else ProjectedPoint(targetCoord.x, targetCoord.y, targetCoord.z)
        } catch (e: Proj4jException) {
            e.printStackTrace()
            null
        }
    }

    fun findCrsById(id: String): CrsData? {
        return predefinedSystems.find { it.id == id }
    }
}
