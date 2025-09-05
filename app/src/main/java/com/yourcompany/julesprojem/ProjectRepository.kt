package com.yourcompany.julesprojem

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("StaticFieldLeak")
object ProjectRepository {

    private lateinit var context: Context
    private lateinit var projectDir: File
    private val gson = Gson()

    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject = _activeProject.asStateFlow()

    private val _stakeoutTarget = MutableStateFlow<Point?>(null)
    val stakeoutTarget = _stakeoutTarget.asStateFlow()

    fun setStakeoutTarget(point: Point) {
        _stakeoutTarget.value = point
    }

    fun init(context: Context) {
        this.context = context
        projectDir = File(context.filesDir, "ProjectData")
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
    }

    fun saveProject(project: Project) {
        val projectFile = File(projectDir, "${project.name}.json")
        projectFile.writeText(gson.toJson(project))
        if(_activeProject.value?.name == project.name) {
            _activeProject.value = project
        }
    }

    fun loadProject(projectName: String): Project? {
        val projectFile = File(projectDir, "$projectName.json")
        val project = if (projectFile.exists()) {
            gson.fromJson(projectFile.readText(), Project::class.java)
        } else {
            null
        }
        _activeProject.value = project
        return project
    }

    fun listProjects(): List<String> {
        return projectDir.listFiles { _, name -> name.endsWith(".json") }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    fun deleteProject(projectName: String): Boolean {
        val projectFile = File(projectDir, "$projectName.json")
        val deleted = if (projectFile.exists()) {
            projectFile.delete()
        } else {
            false
        }
        if (deleted && _activeProject.value?.name == projectName) {
            _activeProject.value = null
        }
        return deleted
    }
}
