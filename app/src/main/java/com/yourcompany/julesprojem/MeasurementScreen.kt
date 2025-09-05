package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MeasurementAction(
    val title: String,
    val icon: ImageVector,
    val action: () -> Unit
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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val measurementActions = listOf(
        MeasurementAction(stringResource(R.string.point), Icons.Default.LocationOn) {
            viewModel.savePoint()
            scope.launch {
                snackbarHostState.showSnackbar("Nokta kaydedildi!")
            }
        },
        MeasurementAction(stringResource(R.string.detail), Icons.AutoMirrored.Filled.List) { /* TODO */ },
        MeasurementAction(stringResource(R.string.photogrammetry), Icons.Default.CameraAlt) { /* TODO */ },
        MeasurementAction(stringResource(R.string.laser), Icons.Default.SquareFoot) { /* TODO */ },
        MeasurementAction(stringResource(R.string.line), Icons.Default.Timeline) { /* TODO */ },
        MeasurementAction(stringResource(R.string.cross_section), Icons.Default.Stairs) { /* TODO */ },
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.measurement)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Main measurement action */ }) {
                Icon(Icons.Default.MyLocation, contentDescription = stringResource(R.string.measure))
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
                            IconButton(onClick = action.action) {
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
