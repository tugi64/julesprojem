package com.yourcompany.julesprojem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import com.yourcompany.julesprojem.coords.CrsData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrsSelectionScreen(
    navController: NavController,
    onCrsSelected: (CrsData) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allSystems = remember { CoordinateSystemManager.predefinedSystems }
    val filteredSystems by remember(searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                allSystems
            } else {
                allSystems.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.id.contains(searchQuery, ignoreCase = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Coordinate System") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by name or EPSG code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(filteredSystems) { crs ->
                    ListItem(
                        headlineContent = { Text(crs.name) },
                        supportingContent = { Text(crs.id) },
                        modifier = Modifier.clickable {
                            onCrsSelected(crs)
                            navController.popBackStack()
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
