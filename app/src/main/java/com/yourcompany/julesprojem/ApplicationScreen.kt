package com.yourcompany.julesprojem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

data class CadAction(
    val title: String,
    val icon: ImageVector
)

@Composable
fun ApplicationRoute(
    gnssViewModel: GnssViewModel = viewModel(
        factory = GnssViewModel.GnssViewModelFactory(
            BluetoothService(LocalContext.current.applicationContext),
            NtripClient()
        )
    ),
    appViewModel: ApplicationViewModel = viewModel()
) {
    ApplicationScreen(gnssViewModel, appViewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen(
    gnssViewModel: GnssViewModel,
    appViewModel: ApplicationViewModel
) {
    val ggaData by gnssViewModel.ggaData.collectAsState()
    val stakeoutTarget by appViewModel.stakeoutTarget.collectAsState()
    val guidance by appViewModel.guidance.collectAsState()

    LaunchedEffect(ggaData) {
        appViewModel.updateLocation(ggaData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.application)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
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

                    // Add user location marker
                    ggaData?.let {
                        val userPoint = GeoPoint(it.latitude, it.longitude)
                        val userMarker = Marker(mapView)
                        userMarker.position = userPoint
                        userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        mapView.overlays.add(userMarker)
                        mapView.controller.animateTo(userPoint)
                    }

                    // Add stakeout target marker
                    stakeoutTarget?.let {
                        val targetPoint = GeoPoint(it.northing, it.easting) // Assuming N is lat, E is lon
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
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = String.format(Locale.US, "Mesafe: %.2f m", it.distance),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = String.format(Locale.US, "Yön: %.1f°", it.bearing),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ApplicationScreenPreview() {
    JulesprojemTheme {
        // ApplicationScreen(viewModel = ...)
    }
}
