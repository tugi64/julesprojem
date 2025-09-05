package com.yourcompany.julesprojem

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@Composable
fun CoordinateSystemRoute(
    viewModel: CoordinateSystemViewModel = viewModel(
        factory = CoordinateSystemViewModel.CoordinateSystemViewModelFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    CoordinateSystemScreen(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoordinateSystemScreen(viewModel: CoordinateSystemViewModel) {
    val datums = listOf("ITRF96", "ED50", "WGS84")
    val projections = listOf("UTM", "3 Derece", "Lambert")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Koordinat Sistemi") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    Button(onClick = { viewModel.saveCoordinateSystem() }) {
                        Text("Kaydet")
                    }
                }
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
                        options = datums.map { it to it },
                        selectedOption = viewModel.selectedDatum,
                        onOptionSelected = { viewModel.selectedDatum = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelector(
                        label = "Projeksiyon Sistemi",
                        options = projections.map { it to it },
                        selectedOption = viewModel.selectedProjection,
                        onOptionSelected = { viewModel.selectedProjection = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.dom,
                        onValueChange = { viewModel.dom = it },
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
        // CoordinateSystemScreen(viewModel = ...)
    }
}
