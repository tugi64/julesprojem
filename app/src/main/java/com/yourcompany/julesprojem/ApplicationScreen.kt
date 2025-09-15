package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.julesprojem.ui.components.GnssStatusHeader
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

@Composable
fun ApplicationRoute(
    navController: NavController,
    gnssViewModel: GnssViewModel
) {
    val appViewModel: ApplicationViewModel = viewModel()
    ApplicationScreen(
        gnssViewModel = gnssViewModel,
        appViewModel = appViewModel,
        onNavigateBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    gnssViewModel: GnssViewModel,
    appViewModel: ApplicationViewModel,
    onNavigateBack: () -> Unit
) {
    val ggaData by gnssViewModel.ggaData.collectAsState()
    val activeProject by ProjectRepository.activeProject.collectAsState()
    val stakeoutTarget by appViewModel.stakeoutTarget.collectAsState()
    val guidance by appViewModel.guidance.collectAsState()

    LaunchedEffect(ggaData) {
        appViewModel.updateLocation(ggaData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Stakeout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
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
                            val userPoint = GeoPoint(it.latitude, it.longitude)
                            val userMarker = Marker(mapView)
                            userMarker.position = userPoint
                            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            mapView.overlays.add(userMarker)
                            mapView.controller.animateTo(userPoint)
                        }

                        stakeoutTarget?.let {
                            val targetPoint = GeoPoint(it.latitude, it.longitude)
                            val targetMarker = Marker(mapView)
                            targetMarker.position = targetPoint
                            targetMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            targetMarker.icon = mapView.context.getDrawable(org.osmdroid.library.R.drawable.marker_default_focused_base)
                            mapView.overlays.add(targetMarker)
                        }
                        mapView.invalidate()
                    }
                )

                guidance?.let {
                    Card(
                        modifier = Modifier.align(Alignment.TopCenter).padding(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = String.format(Locale.US, "Distance: %.2f m", it.distance),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = String.format(Locale.US, "Bearing: %.1f°", it.bearing),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
