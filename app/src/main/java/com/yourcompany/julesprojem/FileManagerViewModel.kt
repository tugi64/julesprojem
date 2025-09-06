package com.yourcompany.julesprojem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FileManagerViewModel : ViewModel() {

    private val _projects = MutableStateFlow<List<String>>(emptyList())
    val projects: StateFlow<List<String>> = _projects.asStateFlow()

    val activeProject: StateFlow<Project?> = ProjectRepository.activeProject

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            _projects.value = ProjectRepository.listProjects()
        }
    }

    fun createNewProject(projectName: String, crsId: String) {
        if (projectName.isBlank()) return
        val newProject = Project(name = projectName, crsId = crsId)
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
