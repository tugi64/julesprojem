package com.yourcompany.julesprojem

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yourcompany.julesprojem.ui.theme.JulesprojemTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileManagerScreen() {
    val files = listOf(
        "Proje_A.xyz",
        "arazi.dxf",
        "yol.csv",
        "Ham_Veri_1.ubx",
        "Lidar_Scan_01.laz"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dosya Yönetimi") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
        floatingActionButton = {
            Row {
                FloatingActionButton(onClick = { /* TODO */ }, modifier = Modifier.padding(end = 8.dp)) {
                    Icon(Icons.Default.CreateNewFolder, contentDescription = "Yeni Proje")
                }
                FloatingActionButton(onClick = { /* TODO */ }) {
                    Icon(Icons.Default.FileOpen, contentDescription = "Proje Aç")
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text("Proje Dosyaları", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(files) { fileName ->
                    ListItem(
                        headlineContent = { Text(fileName) },
                        leadingContent = {
                            Icon(
                                Icons.Default.Folder,
                                contentDescription = "File"
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { /* TODO */ }) {
                    Text("İçe Aktar")
                }
                Button(onClick = { /* TODO */ }) {
                    Text("Dışa Aktar")
                }
                Button(onClick = { /* TODO */ }) {
                    Text("Paylaş")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FileManagerScreenPreview() {
    JulesprojemTheme {
        FileManagerScreen()
    }
}
