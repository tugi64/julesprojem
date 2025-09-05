package com.yourcompany.julesprojem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

data class CadAction(
    val title: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScreen() {
    val applicationActions = listOf(
        CadAction(stringResource(R.string.stakeout_point), Icons.Default.LocationOn),
        CadAction(stringResource(R.string.stakeout_line), Icons.Default.Timeline),
        CadAction(stringResource(R.string.stakeout_cross_section), Icons.Default.Stairs),
    )
    val cadTools = listOf(
        CadAction(stringResource(R.string.snap_end), Icons.Default.AddLocation),
        CadAction(stringResource(R.string.snap_mid), Icons.Default.AddLocationAlt),
        CadAction(stringResource(R.string.ruler), Icons.Default.SquareFoot),
        CadAction(stringResource(R.string.tolerance), Icons.Default.ControlPointDuplicate),
        CadAction(stringResource(R.string.touch), Icons.Default.TouchApp),
    )

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
            // Map/CAD Placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.map_cad_area), style = MaterialTheme.typography.headlineMedium)
            }

            // Application Actions Toolbar
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp)
            ) {
                applicationActions.forEach { action ->
                    FloatingActionButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Icon(action.icon, contentDescription = action.title)
                    }
                }
            }

            // CAD Tools Toolbar
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp)
            ) {
                cadTools.forEach { tool ->
                    FloatingActionButton(
                        onClick = { /* TODO */ },
                        modifier = Modifier.padding(vertical = 4.dp),
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Icon(tool.icon, contentDescription = tool.title)
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
        ApplicationScreen()
    }
}
