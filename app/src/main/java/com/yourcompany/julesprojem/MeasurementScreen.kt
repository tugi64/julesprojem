package com.yourcompany.julesprojem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

data class MeasurementAction(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen() {
    val measurementActions = listOf(
        MeasurementAction("Nokta", Icons.Default.LocationOn),
        MeasurementAction("Detay", Icons.Default.List),
        MeasurementAction("Fotogrametri", Icons.Default.CameraAlt),
        MeasurementAction("Lazer", Icons.Default.SquareFoot),
        MeasurementAction("Hat", Icons.Default.Timeline),
        MeasurementAction("En Kesit", Icons.Default.Stairs),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ölçüm") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Main measurement action */ }) {
                Icon(Icons.Default.MyLocation, contentDescription = "Ölçüm Yap")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Map Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Harita Alanı", style = MaterialTheme.typography.headlineMedium)
            }

            // Measurement Actions Toolbar
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    measurementActions.forEach { action ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(action.icon, contentDescription = action.title)
                            }
                            Text(text = action.title, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeasurementScreenPreview() {
    JulesprojemTheme {
        MeasurementScreen()
    }
}
