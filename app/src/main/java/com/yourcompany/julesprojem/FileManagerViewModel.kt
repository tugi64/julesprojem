package com.yourcompany.julesprojem

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FileManagerViewModel(application: Application) : AndroidViewModel(application) {

    private val projectRepository = ProjectRepository(application.applicationContext)

    val projects = mutableStateOf<List<String>>(emptyList())
    var activeProject = mutableStateOf<Project?>(null)
        private set

    init {
        loadProjects()
    }

    private fun loadProjects() {
        projects.value = projectRepository.listProjects()
    }

    fun createNewProject(projectName: String) {
        val defaultCoordSystem = CoordinateSystem("WGS84", "WGS84", "UTM")
        val newProject = Project(name = projectName, coordinateSystem = defaultCoordSystem)
        projectRepository.saveProject(newProject)
        loadProjects() // Refresh the list
        openProject(projectName)
    }

    fun openProject(projectName: String) {
        activeProject.value = projectRepository.loadProject(projectName)
    }

    fun deleteProject(projectName: String) {
        projectRepository.deleteProject(projectName)
        loadProjects() // Refresh the list
        if (activeProject.value?.name == projectName) {
            activeProject.value = null
        }
    }

    @Suppress("UNCHECKED_CAST")
    class FileManagerViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FileManagerViewModel::class.java)) {
                return FileManagerViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
