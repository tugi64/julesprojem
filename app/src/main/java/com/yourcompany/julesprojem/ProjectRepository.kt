package com.yourcompany.julesprojem

import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

object ProjectRepository {

    private val gson = Gson()
    private val projectsDir = File(TugisGnssApplication.appContext.filesDir, "projects")

    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject = _activeProject.asStateFlow()

    // Added for stakeout functionality
    private val _stakeoutTarget = MutableStateFlow<Point?>(null)
    val stakeoutTarget = _stakeoutTarget.asStateFlow()

    init {
        if (!projectsDir.exists()) {
            projectsDir.mkdirs()
        }
    }

    fun saveProject(project: Project) {
        val projectFile = File(projectsDir, "${project.name}.json")
        projectFile.writeText(gson.toJson(project))
        // If the saved project is the active one, update the flow
        if (_activeProject.value?.name == project.name) {
            _activeProject.value = project
        }
    }

    fun loadProject(projectName: String): Project? {
        val projectFile = File(projectsDir, "$projectName.json")
        return if (projectFile.exists()) {
            val project = gson.fromJson(projectFile.readText(), Project::class.java)
            _activeProject.value = project
            project
        } else {
            null
        }
    }

    fun deleteProject(projectName: String) {
        val projectFile = File(projectsDir, "$projectName.json")
        if (projectFile.exists()) {
            projectFile.delete()
        }
        if (_activeProject.value?.name == projectName) {
            _activeProject.value = null
        }
    }

    fun listProjects(): List<String> {
        return projectsDir.listFiles { _, name -> name.endsWith(".json") }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    fun setStakeoutTarget(point: Point) {
        _stakeoutTarget.value = point
    }
}
