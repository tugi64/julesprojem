package com.yourcompany.julesprojem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JulesprojemTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(navController)
                    }
                    composable("gnss") { GnssConnectionScreen() }
                    composable("measurement") { MeasurementScreen() }
                    composable("application") { ApplicationScreen() }
                    composable("coords") { CoordinateSystemScreen() }
                    composable("layers") { MapLayersScreen() }
                    composable("lidar") { DroneLidarScreen() }
                    composable("radio") { RadioSettingsScreen() }
                    composable("files") { FileManagerScreen() }
                }
            }
        }
    }
}