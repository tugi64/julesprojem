package com.yourcompany.julesprojem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.julesprojem.coords.CoordinateSystemManager

@Composable
fun FileManagerRoute(
    navController: NavController,
    viewModel: FileManagerViewModel = viewModel()
) {
    FileManagerScreen(
        viewModel = viewModel,
        onStakeoutClicked = { /* TODO */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel,
    onStakeoutClicked: (Point) -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    var showNewProjectDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("File Management")
                        activeProject?.let {
                            val crsName = CoordinateSystemManager.findCrsById(it.crsId)?.name ?: it.crsId
                            Text(
                                text = "Active: ${it.name} ($crsName)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewProjectDialog = true }) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = "New Project")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (showNewProjectDialog) {
                NewProjectDialog(
                    onDismiss = { showNewProjectDialog = false },
                    onCreate = { projectName, crsId ->
                        viewModel.createNewProject(projectName, crsId)
                        showNewProjectDialog = false
                    }
                )
            }

            if (activeProject == null) {
                Text("Please open or create a project.")
            } else {
                LazyColumn {
                    item {
                        Text("Project Points", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(activeProject!!.points) { point ->
                        ListItem(
                            headlineContent = { Text(point.name) },
                            supportingContent = {
                                Text("Lat: %.6f, Lon: %.6f, Alt: %.2f".format(point.latitude, point.longitude, point.altitude))
                            },
                            leadingContent = {
                                Icon(Icons.Default.LocationOn, contentDescription = "Point")
                            }
                        )
                    }
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text("All Projects", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(projects) { projectName ->
                        ListItem(
                            headlineContent = { Text(projectName) },
                            leadingContent = {
                                Icon(Icons.Default.Folder, contentDescription = "Project")
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.deleteProject(projectName) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            },
                            modifier = Modifier.clickable { viewModel.openProject(projectName) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    val crsOptions = remember { CoordinateSystemManager.predefinedSystems }
    var expanded by remember { mutableStateOf(false) }
    var selectedCrs by remember { mutableStateOf(crsOptions.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Project") },
        text = {
            Column {
                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCrs.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Coordinate System") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        crsOptions.forEach { crs ->
                            DropdownMenuItem(
                                text = { Text(crs.name) },
                                onClick = {
                                    selectedCrs = crs
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        onCreate(projectName, selectedCrs.id)
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
