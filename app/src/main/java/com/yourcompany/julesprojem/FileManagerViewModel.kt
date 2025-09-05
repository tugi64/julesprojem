package com.yourcompany.julesprojem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*

class FileManagerViewModel : ViewModel() {

    private val _projects = MutableStateFlow<List<String>>(emptyList())
    val projects: StateFlow<List<String>> = _projects

    val activeProject: StateFlow<Project?> = ProjectRepository.activeProject

    init {
        loadProjects()
    }

    private fun loadProjects() {
        _projects.value = ProjectRepository.listProjects()
    }

    fun createNewProject(projectName: String) {
        val defaultCoordSystem = CoordinateSystem("WGS84", "WGS84", "UTM")
        val newProject = Project(name = projectName, coordinateSystem = defaultCoordSystem)
        ProjectRepository.saveProject(newProject)
        loadProjects() // Refresh the list
        openProject(projectName)
    }

    fun openProject(projectName: String) {
        ProjectRepository.loadProject(projectName)
    }

    fun deleteProject(projectName: String) {
        ProjectRepository.deleteProject(projectName)
        loadProjects() // Refresh the list
    }
}
