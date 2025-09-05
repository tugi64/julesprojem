package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                title = { Text(stringResource(R.string.drone_lidar)) },
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
                    Text(stringResource(R.string.drone_and_sensor), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = stringResource(R.string.drone_selection),
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
                    Text(stringResource(R.string.flight_plan_and_data), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* TODO */ }) {
                        Text(stringResource(R.string.load_flight_plan))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO */ }) {
                        Text(stringResource(R.string.import_data))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { /* TODO */ }) {
                        Text(stringResource(R.string.point_cloud_preview))
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
