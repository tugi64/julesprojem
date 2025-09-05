package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLayersScreen() {
    var selectedBaseMap by remember { mutableStateOf("OpenStreetMap") }
    val baseMaps = listOf("OpenStreetMap", "Google Maps", "Bing Maps")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Harita Katmanları") },
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
                    Text("Altlık Harita", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Harita Seçimi",
                        options = baseMaps.map { it to it },
                        selectedOption = selectedBaseMap,
                        onOptionSelected = { selectedBaseMap = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LayerList(title = "Raster Katmanlar", layers = listOf("Ortofoto 2023", "Halihazır Harita"))
            Spacer(modifier = Modifier.height(16.dp))
            LayerList(title = "CAD Dosyaları", layers = listOf("proje.dxf", "parsel.dwg"))
            Spacer(modifier = Modifier.height(16.dp))
            LayerList(title = "Vektör Katmanlar", layers = listOf("yollar.shp", "binalar.kml"))
        }
    }
}

@Composable
fun LayerList(title: String, layers: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            layers.forEach { layerName ->
                LayerItem(layerName = layerName)
            }
        }
    }
}

@Composable
fun LayerItem(layerName: String) {
    var isVisible by remember { mutableStateOf(true) }
    var transparency by remember { mutableStateOf(0f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(layerName, modifier = Modifier.weight(1f))
        Switch(checked = isVisible, onCheckedChange = { isVisible = it })
    }
    Slider(value = transparency, onValueChange = { transparency = it })
}

@Preview(showBackground = true)
@Composable
fun MapLayersScreenPreview() {
    JulesprojemTheme {
        MapLayersScreen()
    }
}
