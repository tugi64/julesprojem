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
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@Composable
fun FileManagerRoute(
    navController: NavController,
    viewModel: FileManagerViewModel = viewModel()
) {
    FileManagerScreen(
        viewModel = viewModel,
        onStakeoutClicked = { point ->
            ProjectRepository.setStakeoutTarget(point)
            navController.navigate("application")
        }
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
                        Text(stringResource(R.string.file_management))
                        activeProject?.let {
                            Text(
                                text = stringResource(R.string.active_project, it.name),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewProjectDialog = true }) {
                Icon(Icons.Default.CreateNewFolder, contentDescription = stringResource(R.string.new_project))
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            if (showNewProjectDialog) {
                NewProjectDialog(
                    onDismiss = { showNewProjectDialog = false },
                    onCreate = { projectName ->
                        viewModel.createNewProject(projectName)
                        showNewProjectDialog = false
                    }
                )
            }

            if (activeProject == null) {
                Text("Lütfen bir proje açın veya oluşturun.")
            } else {
                LazyColumn {
                    item {
                        Text("Proje Noktaları", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(activeProject!!.points) { point ->
                        ListItem(
                            headlineContent = { Text(point.name) },
                            supportingContent = { Text("N: ${point.northing}, E: ${point.easting}, H: ${point.elevation}")},
                            leadingContent = {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Nokta"
                                )
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = { onStakeoutClicked(point) }) {
                                        Icon(Icons.Default.PinDrop, contentDescription = "Aplikasyon")
                                    }
                                    IconButton(onClick = { /* TODO: Delete Point */ }) {
                                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                                    }
                                }
                            }
                        )
                    }
                    item {
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Text("Tüm Projeler", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(projects) { projectName ->
                        ListItem(
                            headlineContent = { Text(projectName) },
                            leadingContent = {
                                Icon(
                                    Icons.Default.Folder,
                                    contentDescription = stringResource(R.string.project_files)
                                )
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.deleteProject(projectName) }) {
                                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
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

@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_new_project)) },
        text = {
            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text(stringResource(R.string.project_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (projectName.isNotBlank()) {
                        onCreate(projectName)
                    }
                }
            ) {
                Text(stringResource(R.string.create))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun FileManagerScreenPreview() {
    JulesprojemTheme {
        // Can't preview this screen easily as it depends on a ViewModel with context
        // FileManagerScreen(viewModel = ...)
    }
}
