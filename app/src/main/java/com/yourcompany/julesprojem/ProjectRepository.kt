package com.yourcompany.julesprojem

import android.content.Context
import com.google.gson.Gson
import java.io.File

class ProjectRepository(private val context: Context) {

    private val projectDir = File(context.filesDir, "ProjectData")
    private val gson = Gson()

    init {
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }
    }

    fun saveProject(project: Project) {
        val projectFile = File(projectDir, "${project.name}.json")
        projectFile.writeText(gson.toJson(project))
    }

    fun loadProject(projectName: String): Project? {
        val projectFile = File(projectDir, "$projectName.json")
        return if (projectFile.exists()) {
            gson.fromJson(projectFile.readText(), Project::class.java)
        } else {
            null
        }
    }

    fun listProjects(): List<String> {
        return projectDir.listFiles { _, name -> name.endsWith(".json") }
            ?.map { it.nameWithoutExtension }
            ?: emptyList()
    }

    fun deleteProject(projectName: String): Boolean {
        val projectFile = File(projectDir, "$projectName.json")
        return if (projectFile.exists()) {
            projectFile.delete()
        } else {
            false
        }
    }
}
