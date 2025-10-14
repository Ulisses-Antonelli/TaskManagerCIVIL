package com.project.taskmanagercivil.presentation.screens.projects

import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProjectDetailUiState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository,
    private val projectId: String
) {
    private val viewModelScope = CoroutineScope(SupervisorJob())

    private val _uiState = MutableStateFlow(ProjectDetailUiState())
    val uiState: StateFlow<ProjectDetailUiState> = _uiState.asStateFlow()

    init {
        loadProjectDetails()
    }

    private fun loadProjectDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val project = projectRepository.getProjectById(projectId)
                val tasks = projectRepository.getTasksByProject(projectId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        project = project,
                        tasks = tasks
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Falha ao carregar detalhes do projeto: ${e.message}"
                    )
                }
            }
        }
    }

    fun refresh() {
        loadProjectDetails()
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            try {
                projectRepository.deleteProject(projectId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao deletar projeto: ${e.message}")
                }
            }
        }
    }
}
