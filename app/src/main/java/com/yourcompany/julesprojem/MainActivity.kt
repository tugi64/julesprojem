package com.yourcompany.julesprojem

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

class MainActivity : ComponentActivity() {

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                // Handle permission granted/denied if needed
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }

        setContent {
            JulesprojemTheme {
                val navController = rememberNavController()

                val gnssViewModel: GnssViewModel = viewModel(
                    factory = GnssViewModel.GnssViewModelFactory(
                        BluetoothService(LocalContext.current.applicationContext),
                        NtripClient()
                    )
                )

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") { MainScreen(navController) }
                    composable("files") { FileManagerRoute(navController) }
                    composable("measurement") { MeasurementRoute(navController, gnssViewModel) }
                    composable("application") { ApplicationRoute(navController, gnssViewModel) }
                    composable("gnss") { GnssConnectionRoute(navController, gnssViewModel) }
                    composable("crs_selection") {
                        CrsSelectionScreen(navController) { selectedCrs ->
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selectedCrsId", selectedCrs.id)
                        }
                    }
                    composable("layers") { MapLayersScreen(navController) }
                    composable("lidar") { DroneLidarScreen(navController) }
                    composable("radio") { RadioSettingsScreen(navController) }
                    composable("settings") { SettingsScreen(navController) }
                }
            }
        }
    }
}