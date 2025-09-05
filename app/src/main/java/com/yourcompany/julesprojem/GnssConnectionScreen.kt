package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GnssConnectionScreen() {
    var selectedManufacturer by remember { mutableStateOf("South") }
    var selectedModel by remember { mutableStateOf("ALPS2") }
    var bluetoothDevice by remember { mutableStateOf("HC-05") }
    var rtkMode by remember { mutableStateOf("RTK") }
    var corsHost by remember { mutableStateOf("") }
    var corsPort by remember { mutableStateOf("") }
    var corsUser by remember { mutableStateOf("") }
    var corsPass by remember { mutableStateOf("") }

    val manufacturers = listOf("South", "Trimble", "Topcon", "Hi-Target", "Sokkia", "Leica")
    val models = listOf("ALPS2", "G1", "G6")
    val bluetoothDevices = listOf("HC-05", "BT-123", "GNSS-ROVER-XYZ")
    val rtkModes = listOf("RTK", "PPK")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GNSS Bağlantısı") },
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
            // GNSS Device Selection
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cihaz Ayarları", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Üretici",
                        options = manufacturers,
                        selectedOption = selectedManufacturer,
                        onOptionSelected = { selectedManufacturer = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelector(
                        label = "Model",
                        options = models,
                        selectedOption = selectedModel,
                        onOptionSelected = { selectedModel = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelector(
                        label = "Bluetooth Cihazı",
                        options = bluetoothDevices,
                        selectedOption = bluetoothDevice,
                        onOptionSelected = { bluetoothDevice = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Connection Mode
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Ölçüm Modu", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Mod",
                        options = rtkModes,
                        selectedOption = rtkMode,
                        onOptionSelected = { rtkMode = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // CORS Settings
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CORS (NTRIP) Ayarları", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = corsHost, onValueChange = { corsHost = it }, label = { Text("Host") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = corsPort, onValueChange = { corsPort = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = corsUser, onValueChange = { corsUser = it }, label = { Text("Kullanıcı Adı") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = corsPass, onValueChange = { corsPass = it }, label = { Text("Şifre") }, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Connection Status and Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Durum: Bağlı Değil", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { /* TODO: Implement connection logic */ }) {
                    Text("BAĞLAN")
                }
            }
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GnssConnectionScreenPreview() {
    JulesprojemTheme {
        GnssConnectionScreen()
    }
}
