package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme
import java.util.Locale

@Composable
fun GnssConnectionRoute(
    viewModel: GnssViewModel = viewModel(
        factory = GnssViewModel.GnssViewModelFactory(
            BluetoothService(LocalContext.current.applicationContext),
            NtripClient()
        )
    )
) {
    GnssConnectionScreen(
        uiState = viewModel,
        onConnectClicked = { viewModel.onConnectClicked() },
        onScanClicked = { viewModel.scanForDevices() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GnssConnectionScreen(
    uiState: GnssUiState,
    onConnectClicked: () -> Unit,
    onScanClicked: () -> Unit
) {
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
            // Live Data Display
            uiState.ggaData?.let { data ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Canlı Konum Verisi", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(String.format(Locale.US, "Enlem: %.6f", data.latitude))
                        Text(String.format(Locale.US, "Boylam: %.6f", data.longitude))
                        Text("Yükseklik: ${data.altitude} m")
                        Text("Uydu Sayısı: ${data.satelliteCount}")
                        Text("Düzeltme Durumu: ${data.fixQuality}")
                        Text("HDOP: ${data.hdop}")
                    }
                }
            }

            // GNSS Device Selection
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Cihaz Ayarları", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    DropdownSelector(
                        label = "Üretici",
                        options = uiState.manufacturers.map { it to it },
                        selectedOption = uiState.selectedManufacturer,
                        onOptionSelected = { uiState.selectedManufacturer = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownSelector(
                        label = "Model",
                        options = uiState.models.map { it to it },
                        selectedOption = uiState.selectedModel,
                        onOptionSelected = { uiState.selectedModel = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) {
                            DropdownSelector(
                                label = "Bluetooth Cihazı",
                                options = uiState.bluetoothDevices.value,
                                selectedOption = uiState.selectedBluetoothDevice,
                                onOptionSelected = { uiState.selectedBluetoothDevice = it }
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(48.dp)) {
                            if (uiState.isScanning) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp).align(Alignment.Center))
                            } else {
                                IconButton(onClick = onScanClicked) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Scan for devices")
                                }
                            }
                        }
                    }
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
                        options = uiState.rtkModes.map { it to it },
                        selectedOption = uiState.rtkMode,
                        onOptionSelected = { uiState.rtkMode = it }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // CORS Settings
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CORS (NTRIP) Ayarları", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(value = uiState.corsHost, onValueChange = { uiState.corsHost = it }, label = { Text("Host") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.corsPort, onValueChange = { uiState.corsPort = it }, label = { Text("Port") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.corsUser, onValueChange = { uiState.corsUser = it }, label = { Text("Kullanıcı Adı") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = uiState.corsPass, onValueChange = { uiState.corsPass = it }, label = { Text("Şifre") }, modifier = Modifier.fillMaxWidth())
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Connection Status and Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Durum: ${uiState.connectionStatus}", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = onConnectClicked) {
                    Text("BAĞLAN")
                }
            }
        }
    }
}

interface GnssUiState {
    var selectedManufacturer: String
    var selectedModel: String
    var selectedBluetoothDevice: String
    var rtkMode: String
    var corsHost: String
    var corsPort: String
    var corsUser: String
    var corsPass: String
    val connectionStatus: String
    val isScanning: Boolean
    val ggaData: GgaData?
    val manufacturers: List<String>
    val models: List<String>
    val bluetoothDevices: State<List<Pair<String, String>>>
    val rtkModes: List<String>
}

@Composable
fun DropdownSelector(
    label: String,
    options: List<Pair<String, String>>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOptionName = options.find { it.second == selectedOption }?.first ?: ""

    Box {
        OutlinedTextField(
            value = selectedOptionName,
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
                    text = { Text(option.first) },
                    onClick = {
                        onOptionSelected(option.second)
                        expanded = false
                    }
                )
            }
        }
    }
}

class FakeGnssUiState : GnssUiState {
    override var selectedManufacturer by mutableStateOf("Fake Manufacturer")
    override var selectedModel by mutableStateOf("Fake Model")
    override var selectedBluetoothDevice by mutableStateOf("00:11:22:33:44:55")
    override var rtkMode by mutableStateOf("RTK")
    override var corsHost by mutableStateOf("fake.host.com")
    override var corsPort by mutableStateOf("2101")
    override var corsUser by mutableStateOf("fakeuser")
    override var corsPass by mutableStateOf("fakepass")
    override val connectionStatus by mutableStateOf("Not Connected")
    override val isScanning by mutableStateOf(false)
    override var ggaData by mutableStateOf<GgaData?>(null)
    override val manufacturers = listOf("Fake Manufacturer", "Another Fake")
    override val models = listOf("Fake Model 1", "Fake Model 2")
    override val bluetoothDevices = mutableStateOf(listOf("Fake BT 1" to "00:11:22:33:44:55", "Fake BT 2" to "AA:BB:CC:DD:EE:FF"))
    override val rtkModes = listOf("RTK", "PPK")
}

@Preview(showBackground = true)
@Composable
fun GnssConnectionScreenPreview() {
    JulesprojemTheme {
        GnssConnectionScreen(
            uiState = FakeGnssUiState(),
            onConnectClicked = {},
            onScanClicked = {}
        )
    }
}
