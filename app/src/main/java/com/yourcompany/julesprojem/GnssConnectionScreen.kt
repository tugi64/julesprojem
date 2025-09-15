package com.yourcompany.julesprojem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun GnssConnectionRoute(
    navController: NavController,
    gnssViewModel: GnssViewModel
) {
    val factory = GnssConnectionViewModelFactory(BluetoothService(LocalContext.current.applicationContext), gnssViewModel)
    val connectionViewModel: GnssConnectionViewModel = viewModel(factory = factory)

    GnssConnectionScreen(
        viewModel = connectionViewModel,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GnssConnectionScreen(
    viewModel: GnssConnectionViewModel,
    onNavigateBack: () -> Unit
) {
    val pairedDevices by remember { derivedStateOf { viewModel.pairedDevices } }
    val scannedDevices by remember { derivedStateOf { viewModel.scannedDevices } }
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val isConnecting by viewModel.isConnecting.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("GNSS Connection") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(text = "Status: $connectionStatus", style = MaterialTheme.typography.titleMedium)
            if (isConnecting) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { if (!isScanning) viewModel.scanForDevices() else viewModel.stopScanning() },
                    enabled = !isConnecting
                ) {
                    Text(if (!isScanning) "Scan for Devices" else "Stop Scanning")
                }
                if (isScanning) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(start = 16.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Paired Devices", style = MaterialTheme.typography.titleSmall)
            DeviceList(devices = pairedDevices, onDeviceClick = { viewModel.connectToDevice(it.second) }, enabled = !isConnecting)

            Spacer(modifier = Modifier.height(16.dp))
            Text("Scanned Devices", style = MaterialTheme.typography.titleSmall)
            DeviceList(devices = scannedDevices, onDeviceClick = { viewModel.connectToDevice(it.second) }, enabled = !isConnecting)
        }
    }
}

@Composable
private fun DeviceList(devices: List<Pair<String, String>>, onDeviceClick: (Pair<String, String>) -> Unit, enabled: Boolean) {
    if (devices.isEmpty()) {
        Text("No devices found.", style = MaterialTheme.typography.bodySmall)
    } else {
        LazyColumn {
            items(devices) { device ->
                ListItem(
                    headlineContent = { Text(device.first) },
                    supportingContent = { Text(device.second) },
                    modifier = Modifier.clickable(enabled = enabled) { onDeviceClick(device) }
                )
            }
        }
    }
}

class GnssConnectionViewModelFactory(
    private val bluetoothService: BluetoothService,
    private val gnssViewModel: GnssViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GnssConnectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GnssConnectionViewModel(bluetoothService, gnssViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
