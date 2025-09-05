package com.yourcompany.julesprojem

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CoordinateSystemViewModel(application: Application) : AndroidViewModel(application) {

    private val projectRepository = ProjectRepository(application.applicationContext)

    var selectedDatum by mutableStateOf("ITRF96")
    var selectedProjection by mutableStateOf("UTM")
    var dom by mutableStateOf("")

    fun saveCoordinateSystem() {
        viewModelScope.launch {
            // This is a simplified implementation. In a real app, we would update the currently active project.
            // For now, we'll just save a default project to demonstrate the functionality.
            val defaultCoordSystem = CoordinateSystem(
                name = "Default",
                ellipsoid = selectedDatum,
                projection = selectedProjection,
                centralMeridian = dom.toDoubleOrNull()
            )
            val defaultProject = projectRepository.loadProject("Default") ?: Project("Default", defaultCoordSystem)
            val updatedProject = defaultProject.copy(coordinateSystem = defaultCoordSystem)
            projectRepository.saveProject(updatedProject)
        }
    }

    @Suppress("UNCHECKED_CAST")
    class CoordinateSystemViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CoordinateSystemViewModel::class.java)) {
                return CoordinateSystemViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
