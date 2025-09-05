package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DroneLidarScreen() {
    var selectedDrone by remember { mutableStateOf("DJI Matrice 300") }
    val drones = listOf("DJI Matrice 300", "WingtraOne", "eBee X")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Drone LiDAR") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Drone ve Sensör", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Drone Seçimi",
                        options = drones.map { it to it },
                        selectedOption = selectedDrone,
                        onOptionSelected = { selectedDrone = it }
                    )
                    // Placeholder for LiDAR sensor settings
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Uçuş Planı ve Veri", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* TODO */ }) {
                        Text("Uçuş Planı Yükle")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO */ }) {
                        Text("Verileri İçe Aktar")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO */ }) {
                        Text("Nokta Bulutu Önizleme")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DroneLidarScreenPreview() {
    JulesprojemTheme {
        DroneLidarScreen()
    }
}
