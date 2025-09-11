package com.yourcompany.julesprojem

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.yourcompany.julesprojem.coords.CoordinateSystemManager
import com.yourcompany.julesprojem.coords.CrsData
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun FileManagerRoute(
    navController: NavController,
    viewModel: FileManagerViewModel = viewModel()
) {
    // Listen for the result from CrsSelectionScreen
    val newCrsResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("selectedCrsId")
        ?.observeAsState()

    FileManagerScreen(
        viewModel = viewModel,
        navController = navController,
        newCrsResult = newCrsResult
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(
    viewModel: FileManagerViewModel,
    navController: NavController,
    newCrsResult: State<String?>?
) {
    val projects by viewModel.projects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    var showNewProjectDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // ... (rest of the launchers remain the same) ...
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(it)
                val reader = BufferedReader(InputStreamReader(inputStream))
                val content = reader.readText()
                val extension = contentResolver.getType(it)?.split("/")?.last() ?: "txt"
                val result = viewModel.importPoints(content, extension)
                scope.launch {
                    snackbarHostState.showSnackbar(result)
                }
            } catch (e: Exception) {
                scope.launch {
                    snackbarHostState.showSnackbar("Error reading file: ${e.message}")
                }
            }
        }
    }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        uri?.let {
            val content = viewModel.exportPointsAsCsv()
            if (content != null) {
                try {
                    context.contentResolver.openOutputStream(it)?.use { out -> out.write(content.toByteArray()) }
                    scope.launch { snackbarHostState.showSnackbar("Export successful.") }
                } catch (e: Exception) {
                    scope.launch { snackbarHostState.showSnackbar("Error writing file: ${e.message}") }
                }
            } else {
                scope.launch { snackbarHostState.showSnackbar("No points to export.") }
            }
        }
    }

    val exportNcnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain")
    ) { uri: Uri? ->
        uri?.let {
            val content = viewModel.exportPointsAsNcn()
            if (content != null) {
                try {
                    context.contentResolver.openOutputStream(it)?.use { out -> out.write(content.toByteArray()) }
                    scope.launch { snackbarHostState.showSnackbar("Export successful.") }
                } catch (e: Exception) {
                    scope.launch { snackbarHostState.showSnackbar("Error writing file: ${e.message}") }
                }
            } else {
                scope.launch { snackbarHostState.showSnackbar("No points to export.") }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("File Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { importLauncher.launch("*/*") }) {
                        Icon(Icons.Default.UploadFile, contentDescription = "Import")
                    }
                    var showExportMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showExportMenu = true }) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = "Export")
                    }
                    DropdownMenu(
                        expanded = showExportMenu,
                        onDismissRequest = { showExportMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export as CSV/TXT") },
                            onClick = {
                                val projectName = activeProject?.name ?: "export"
                                exportCsvLauncher.launch("$projectName.csv")
                                showExportMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as NCN") },
                            onClick = {
                                val projectName = activeProject?.name ?: "export"
                                exportNcnLauncher.launch("$projectName.ncn")
                                showExportMenu = false
                            }
                        )
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
                    navController = navController,
                    newCrsResult = newCrsResult,
                    onDismiss = { showNewProjectDialog = false },
                    onCreate = { projectName, crsId ->
                        viewModel.createNewProject(projectName, crsId)
                        showNewProjectDialog = false
                    }
                )
            }

            // ... (rest of the screen layout remains the same) ...
            activeProject?.let { project ->
                Text("Active Project: ${project.name} (${CoordinateSystemManager.findCrsById(project.crsId)?.name})", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(200.dp)) { // Added height for demo
                    item {
                        Text("Project Points (${project.points.size})", style = MaterialTheme.typography.titleSmall)
                    }
                    items(project.points) { point ->
                        ListItem(
                            headlineContent = { Text(point.name) },
                            supportingContent = {
                                Text("Lat: %.6f, Lon: %.6f, Alt: %.2f".format(point.latitude, point.longitude, point.altitude))
                            },
                            leadingContent = {
                                Icon(Icons.Default.LocationOn, contentDescription = "Point")
                            },
                            trailingContent = {
                                Row {
                                    IconButton(onClick = {
                                        ProjectRepository.setStakeoutTarget(point)
                                        navController.navigate("application")
                                    }) {
                                        Icon(Icons.Default.PinDrop, contentDescription = "Stakeout Point")
                                    }
                                    IconButton(onClick = { /* TODO: Delete point */ }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete Point")
                                    }
                                }
                            }
                        )
                    }
                }
            } ?: Text("No active project. Open or create a project.")

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Text("All Projects", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(projects) { projectName ->
                    ListItem(
                        headlineContent = { Text(projectName) },
                        leadingContent = { Icon(Icons.Default.Folder, contentDescription = "Project") },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteProject(projectName) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Project")
                            }
                        },
                        modifier = Modifier.clickable { viewModel.openProject(projectName) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NewProjectDialog(
    navController: NavController,
    newCrsResult: State<String?>?,
    onDismiss: () -> Unit,
    onCreate: (String, String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }
    val defaultCrs = remember { CoordinateSystemManager.predefinedSystems.first() }
    var selectedCrs by remember { mutableStateOf(defaultCrs) }

    // Update the selected CRS when we get a result back from the selection screen
    LaunchedEffect(newCrsResult) {
        newCrsResult?.value?.let { crsId ->
            CoordinateSystemManager.findCrsById(crsId)?.let {
                selectedCrs = it
                // Clear the result from the state handle so it's not reused
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("selectedCrsId")
            }
        }
    }

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
                Text("Coordinate System", style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedCrs.name, modifier = Modifier.weight(1f))
                    Button(onClick = { navController.navigate("crs_selection") }) {
                        Text("Change")
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
