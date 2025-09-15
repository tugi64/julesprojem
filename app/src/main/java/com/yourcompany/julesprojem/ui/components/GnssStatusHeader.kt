package com.yourcompany.julesprojem.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.GgaData
import com.yourcompany.julesprojem.Project
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import com.yourcompany.julesprojem.coords.ProjectedPoint
import java.util.Locale

@Composable
fun GnssStatusHeader(
    ggaData: GgaData?,
    project: Project?
) {
    var projectedPoint by remember { mutableStateOf<ProjectedPoint?>(null) }

    LaunchedEffect(ggaData, project) {
        if (ggaData != null && project != null) {
            projectedPoint = CoordinateSystemManager.transformToProjected(
                lat = ggaData.latitude,
                lon = ggaData.longitude,
                alt = ggaData.altitude,
                targetCrsId = project.crsId
            )
        } else {
            projectedPoint = null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoordDisplay("X", projectedPoint?.x, "%.3f")
            CoordDisplay("Y", projectedPoint?.y, "%.3f")
            CoordDisplay("Z", projectedPoint?.z, "%.3f")
            Spacer(modifier = Modifier.width(8.dp))
            GnssQualityDisplay("Sats", ggaData?.satelliteCount?.toString() ?: "-")
            GnssQualityDisplay("HDOP", ggaData?.hdop?.let { String.format(Locale.US, "%.1f", it) } ?: "-")
        }
    }
}

@Composable
private fun CoordDisplay(label: String, value: Double?, format: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value?.let { String.format(Locale.US, format, it) } ?: "---",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GnssQualityDisplay(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
