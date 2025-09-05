package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MeasurementAction(
    val title: String,
    val icon: ImageVector
)

@Composable
fun MeasurementRoute(
    viewModel: GnssViewModel = viewModel(
        factory = GnssViewModel.GnssViewModelFactory(
            BluetoothService(LocalContext.current.applicationContext),
            NtripClient()
        )
    )
) {
    MeasurementScreen(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen(viewModel: GnssViewModel) {
    val ggaData by viewModel.ggaData.collectAsState()

    val measurementActions = listOf(
        MeasurementAction("Nokta", Icons.Default.LocationOn),
        MeasurementAction("Detay", Icons.AutoMirrored.Filled.List),
        MeasurementAction("Fotogrametri", Icons.Default.CameraAlt),
        MeasurementAction("Lazer", Icons.Default.SquareFoot),
        MeasurementAction("Hat", Icons.Default.Timeline),
        MeasurementAction("En Kesit", Icons.Default.Stairs),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ölçüm") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Ayarlar")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Main measurement action */ }) {
                Icon(Icons.Default.MyLocation, contentDescription = "Ölçüm Yap")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
                    MapView(context).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(18.0)
                        controller.setCenter(GeoPoint(41.0, 29.0)) // Default to Istanbul
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()
                    ggaData?.let {
                        val point = GeoPoint(it.latitude, it.longitude)
                        val marker = Marker(mapView)
                        marker.position = point
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        mapView.overlays.add(marker)
                        mapView.controller.animateTo(point)
                    }
                    mapView.invalidate()
                }
            )

            // Measurement Actions Toolbar
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    measurementActions.forEach { action ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(4.dp)
                        ) {
                            IconButton(onClick = { /* TODO */ }) {
                                Icon(action.icon, contentDescription = action.title)
                            }
                            Text(text = action.title, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MeasurementScreenPreview() {
    // This preview will not show the map correctly, but it's for layout purposes.
    // To see the map, you need to run it on an emulator or a real device.
    JulesprojemTheme {
        // MeasurementScreen(viewModel = /* Provide a fake ViewModel for preview */)
    }
}
