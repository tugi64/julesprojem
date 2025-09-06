package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.julesprojem.ui.components.GnssStatusHeader
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MeasurementRoute(
    gnssViewModel: GnssViewModel = viewModel(
        factory = GnssViewModel.GnssViewModelFactory(
            BluetoothService(LocalContext.current.applicationContext),
            NtripClient()
        )
    )
) {
    MeasurementScreen(gnssViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen(viewModel: GnssViewModel) {
    val ggaData by viewModel.ggaData.collectAsState()
    val activeProject by ProjectRepository.activeProject.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Survey") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.savePoint()
                scope.launch {
                    snackbarHostState.showSnackbar("Point saved!")
                }
            }) {
                Icon(Icons.Default.LocationOn, contentDescription = "Save Point")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            GnssStatusHeader(ggaData = ggaData, project = activeProject)

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
                        MapView(context).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
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
            }
        }
    }
}
