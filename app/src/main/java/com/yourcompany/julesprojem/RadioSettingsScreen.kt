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
fun RadioSettingsScreen() {
    var selectedRadio by remember { mutableStateOf("South") }
    var frequency by remember { mutableStateOf("433.000") }
    var baudRate by remember { mutableStateOf("19200") }
    var repeaterMode by remember { mutableStateOf(false) }

    val radios = listOf("South", "Trimble", "Topcon", "Satel")
    val baudRates = listOf("9600", "19200", "38400", "115200")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Telsiz Ayarları") },
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
                    Text("Telsiz Modeli ve Frekans", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Telsiz Marka/Model",
                        options = radios.map { it to it },
                        selectedOption = selectedRadio,
                        onOptionSelected = { selectedRadio = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = frequency,
                        onValueChange = { frequency = it },
                        label = { Text("Frekans (MHz)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Bağlantı Ayarları", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Seri Port Hızı (Baud Rate)",
                        options = baudRates.map { it to it },
                        selectedOption = baudRate,
                        onOptionSelected = { baudRate = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Text("Röle Modu (Repeater)")
                        Switch(checked = repeaterMode, onCheckedChange = { repeaterMode = it })
                    }
                }
            }
             Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { /* TODO */ }) {
                Text("Telsiz Durum Kontrolü")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadioSettingsScreenPreview() {
    JulesprojemTheme {
        RadioSettingsScreen()
    }
}
