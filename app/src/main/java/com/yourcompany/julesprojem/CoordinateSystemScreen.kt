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
fun CoordinateSystemScreen() {
    var selectedDatum by remember { mutableStateOf("ITRF96") }
    var selectedProjection by remember { mutableStateOf("UTM") }
    var dom by remember { mutableStateOf("") }

    val datums = listOf("ITRF96", "ED50", "WGS84")
    val projections = listOf("UTM", "3 Derece", "Lambert")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Koordinat Sistemi") },
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
                    Text("Datum ve Projeksiyon", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Datum",
                        options = datums,
                        selectedOption = selectedDatum,
                        onOptionSelected = { selectedDatum = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelector(
                        label = "Projeksiyon Sistemi",
                        options = projections,
                        selectedOption = selectedProjection,
                        onOptionSelected = { selectedProjection = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = dom,
                        onValueChange = { dom = it },
                        label = { Text("Orta Meridyen (DOM)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dönüşüm Dosyaları", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { /* TODO: Load GRD file */ }) {
                        Text("Grid-GPS Dönüşüm Dosyası Yükle (.grd)")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CoordinateSystemScreenPreview() {
    JulesprojemTheme {
        CoordinateSystemScreen()
    }
}
