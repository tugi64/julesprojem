package com.yourcompany.julesprojem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@Composable
fun FileManagerRoute(
    viewModel: FileManagerViewModel = viewModel(
        factory = FileManagerViewModel.FileManagerViewModelFactory(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    FileManagerScreen(viewModel)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen(viewModel: FileManagerViewModel) {
    val projects by viewModel.projects
    val activeProject by viewModel.activeProject
    var showNewProjectDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Proje Yönetimi")
                        activeProject?.let {
                            Text(
                                text = "Aktif: ${it.name}",
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
                Icon(Icons.Default.CreateNewFolder, contentDescription = "Yeni Proje")
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

            LazyColumn {
                items(projects) { projectName ->
                    ListItem(
                        headlineContent = { Text(projectName) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = "Proje"
                            )
                        },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteProject(projectName) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Sil")
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
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var projectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Proje Oluştur") },
        text = {
            OutlinedTextField(
                value = projectName,
                onValueChange = { projectName = it },
                label = { Text("Proje Adı") },
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
                Text("Oluştur")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("İptal")
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
