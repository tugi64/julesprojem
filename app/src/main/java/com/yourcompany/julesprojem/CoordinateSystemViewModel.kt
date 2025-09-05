package com.yourcompany.julesprojem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CoordinateSystemViewModel : ViewModel() {

    var selectedDatum by mutableStateOf("ITRF96")
    var selectedProjection by mutableStateOf("UTM")
    var dom by mutableStateOf("")

    private var activeProject: Project? = null

    init {
        viewModelScope.launch {
            ProjectRepository.activeProject.collect { project ->
                activeProject = project
                project?.coordinateSystem?.let {
                    selectedDatum = it.ellipsoid
                    selectedProjection = it.projection
                    dom = it.centralMeridian?.toString() ?: ""
                }
            }
        }
    }

    fun saveCoordinateSystem() {
        val projectToUpdate = activeProject ?: return

        viewModelScope.launch {
            val newCoordSystem = CoordinateSystem(
                name = projectToUpdate.coordinateSystem.name,
                ellipsoid = selectedDatum,
                projection = selectedProjection,
                centralMeridian = dom.toDoubleOrNull()
            )
            val updatedProject = projectToUpdate.copy(coordinateSystem = newCoordSystem)
            ProjectRepository.saveProject(updatedProject)
        }
    }
}
