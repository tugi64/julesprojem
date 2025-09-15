package com.yourcompany.julesprojem.fileio

import com.yourcompany.julesprojem.Point

sealed class ImportResult {
    data class Success(val points: List<Point>) : ImportResult()
    data class Error(val message: String) : ImportResult()
}

object ImportExportManager {

    fun importPoints(fileContent: String, fileExtension: String, crsId: String): ImportResult {
        return when (fileExtension.lowercase()) {
            "csv", "txt" -> CsvParser.parse(fileContent, crsId)
            "ncn" -> NcnParser.parse(fileContent, crsId)
            else -> ImportResult.Error("Unsupported file type: $fileExtension")
        }
    }
}
