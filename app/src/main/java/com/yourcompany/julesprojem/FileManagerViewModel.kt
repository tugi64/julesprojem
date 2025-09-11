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

    fun importPoints(fileContent: String, fileExtension: String): String {
        val project = activeProject.value
        if (project == null) {
            return "No active project. Please create or open a project first."
        }

        val result = com.yourcompany.julesprojem.fileio.ImportExportManager.importPoints(
            fileContent,
            fileExtension,
            project.crsId
        )

        return when (result) {
            is com.yourcompany.julesprojem.fileio.ImportResult.Success -> {
                project.points.addAll(result.points)
                ProjectRepository.saveProject(project)
                "${result.points.size} points imported successfully."
            }
            is com.yourcompany.julesprojem.fileio.ImportResult.Error -> {
                result.message
            }
        }
    }

    fun exportPointsAsCsv(): String? {
        val project = activeProject.value ?: return null
        return com.yourcompany.julesprojem.fileio.PointExporter.exportAsCsv(project.points, project.crsId)
    }

    fun exportPointsAsNcn(): String? {
        val project = activeProject.value ?: return null
        return com.yourcompany.julesprojem.fileio.NcnExporter.exportAsNcn(project.points, project.crsId)
    }
}
