package com.project.taskmanagercivil.presentation.screens.projects

import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectsUiState(
    val allProjects: List<Project> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val searchQuery: String = "",
    val selectedLocation: String? = null,
    val sortBy: ProjectSortOption = ProjectSortOption.NAME,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class ProjectSortOption(val label: String) {
    NAME("Nome"),
    START_DATE("Data de Início"),
    END_DATE("Prazo"),
    BUDGET("Orçamento"),
    LOCATION("Localização")
}

class ProjectsViewModel(
    private val projectRepository: ProjectRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob())

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val projects = projectRepository.getAllProjects()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allProjects = projects
                    )
                }
                applyFilters()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Falha ao carregar projetos: ${e.message}"
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilters()
    }

    fun onLocationFilterChange(location: String?) {
        _uiState.update { it.copy(selectedLocation = location) }
        applyFilters()
    }

    fun onSortByChange(sortBy: ProjectSortOption) {
        _uiState.update { it.copy(sortBy = sortBy) }
        applyFilters()
    }

    private fun applyFilters() {
        val currentState = _uiState.value
        var filtered = currentState.allProjects

        // Filtrar por busca
        if (currentState.searchQuery.isNotBlank()) {
            filtered = filtered.filter { project ->
                project.name.contains(currentState.searchQuery, ignoreCase = true) ||
                project.description.contains(currentState.searchQuery, ignoreCase = true) ||
                project.client.contains(currentState.searchQuery, ignoreCase = true)
            }
        }

        // Filtrar por localização
        if (currentState.selectedLocation != null) {
            filtered = filtered.filter { project ->
                project.location == currentState.selectedLocation
            }
        }

        // Ordenar
        filtered = when (currentState.sortBy) {
            ProjectSortOption.NAME -> filtered.sortedBy { it.name }
            ProjectSortOption.START_DATE -> filtered.sortedBy { it.startDate }
            ProjectSortOption.END_DATE -> filtered.sortedBy { it.endDate }
            ProjectSortOption.BUDGET -> filtered.sortedByDescending { it.budget }
            ProjectSortOption.LOCATION -> filtered.sortedBy { it.location }
        }

        _uiState.update { it.copy(filteredProjects = filtered) }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                val success = projectRepository.deleteProject(projectId)
                if (success) {
                    loadProjects()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Falha ao deletar projeto: ${e.message}")
                }
            }
        }
    }
}
