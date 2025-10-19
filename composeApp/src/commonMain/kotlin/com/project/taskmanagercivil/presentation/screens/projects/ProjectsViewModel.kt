package com.project.taskmanagercivil.presentation.screens.projects

import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TaskRepository
import com.project.taskmanagercivil.utils.ProjectStatusUtils
import com.project.taskmanagercivil.utils.calculateDerivedStatus
import com.project.taskmanagercivil.utils.calculateProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectsUiState(
    val allProjects: List<Project> = emptyList(),
    val allTasks: List<Task> = emptyList(),
    val filteredProjects: List<Project> = emptyList(),
    val searchQuery: String = "",
    val selectedLocation: String? = null,
    val selectedProjectStatus: TaskStatus? = null,
    val selectedTaskStatus: TaskStatus? = null,
    val sortBy: ProjectSortOption = ProjectSortOption.NAME,
    val isLoading: Boolean = false,
    val error: String? = null
)

enum class ProjectSortOption(val label: String) {
    NAME("Nome"),
    START_DATE("Data de Início"),
    END_DATE("Prazo"),
    BUDGET("Orçamento"),
    LOCATION("Localização"),
    PROGRESS("Progresso")
}

class ProjectsViewModel(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob())

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val projects = projectRepository.getAllProjects()
                val tasks = taskRepository.getAllTasks()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        allProjects = projects,
                        allTasks = tasks
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

    fun onProjectStatusFilterChange(status: TaskStatus?) {
        _uiState.update { it.copy(selectedProjectStatus = status) }
        applyFilters()
    }

    fun onTaskStatusFilterChange(status: TaskStatus?) {
        _uiState.update { it.copy(selectedTaskStatus = status) }
        applyFilters()
    }

    fun onSortByChange(sortBy: ProjectSortOption) {
        _uiState.update { it.copy(sortBy = sortBy) }
        applyFilters()
    }

    fun clearFilters() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                selectedLocation = null,
                selectedProjectStatus = null,
                selectedTaskStatus = null
            )
        }
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

        // Filtrar por status derivado do projeto
        if (currentState.selectedProjectStatus != null) {
            filtered = ProjectStatusUtils.filterProjectsByDerivedStatus(
                filtered,
                currentState.allTasks,
                currentState.selectedProjectStatus!!
            )
        }

        // Filtrar por status de tarefas internas
        // "Se o usuário aplicar um filtro de 'tarefas em revisão', o sistema deve exibir
        // todas as obras que possuam ao menos uma tarefa com esse status"
        if (currentState.selectedTaskStatus != null) {
            filtered = ProjectStatusUtils.filterProjectsByTaskStatus(
                filtered,
                currentState.allTasks,
                currentState.selectedTaskStatus!!
            )
        }

        // Ordenar
        filtered = when (currentState.sortBy) {
            ProjectSortOption.NAME -> filtered.sortedBy { it.name }
            ProjectSortOption.START_DATE -> filtered.sortedBy { it.startDate }
            ProjectSortOption.END_DATE -> filtered.sortedBy { it.endDate }
            ProjectSortOption.BUDGET -> filtered.sortedByDescending { it.budget }
            ProjectSortOption.LOCATION -> filtered.sortedBy { it.location }
            ProjectSortOption.PROGRESS -> filtered.sortedByDescending { project ->
                project.calculateProgress(currentState.allTasks)
            }
        }

        _uiState.update { it.copy(filteredProjects = filtered) }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                val success = projectRepository.deleteProject(projectId)
                if (success) {
                    loadData()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Falha ao deletar projeto: ${e.message}")
                }
            }
        }
    }

    /**
     * Define um filtro de status derivado do projeto (vindo do Dashboard)
     */
    fun setProjectStatusFilter(status: TaskStatus) {
        onProjectStatusFilterChange(status)
    }
}
