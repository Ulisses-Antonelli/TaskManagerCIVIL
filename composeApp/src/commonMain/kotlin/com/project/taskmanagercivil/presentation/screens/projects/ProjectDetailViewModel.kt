package com.project.taskmanagercivil.presentation.screens.projects

import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TeamRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProjectDetailUiState(
    val project: Project? = null,
    val tasks: List<Task> = emptyList(),
    val availableEmployees: List<Employee> = emptyList(),
    val availableTeams: List<Team> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProjectDetailViewModel(
    private val projectRepository: ProjectRepository,
    private val employeeRepository: EmployeeRepository,
    private val teamRepository: TeamRepository,
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

                // Coleta apenas o primeiro valor dos flows
                val employees = employeeRepository.getAllEmployees().first()
                val teams = teamRepository.getAllTeams().first()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        project = project,
                        tasks = tasks,
                        availableEmployees = employees,
                        availableTeams = teams
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

    fun updateProjectGeneralInfo(name: String, description: String, client: String, location: String, budget: Double) {
        viewModelScope.launch {
            try {
                val currentProject = _uiState.value.project ?: return@launch

                val updatedProject = currentProject.copy(
                    name = name,
                    description = description,
                    client = client,
                    location = location,
                    budget = budget
                )

                // TODO: Implementar no repository quando backend estiver pronto
                // projectRepository.updateProject(updatedProject)

                // Por enquanto, atualiza localmente
                _uiState.update { it.copy(project = updatedProject) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao atualizar informações: ${e.message}")
                }
            }
        }
    }

    fun updateProjectSchedule(startDate: kotlinx.datetime.LocalDate, endDate: kotlinx.datetime.LocalDate) {
        viewModelScope.launch {
            try {
                val currentProject = _uiState.value.project ?: return@launch

                val updatedProject = currentProject.copy(
                    startDate = startDate,
                    endDate = endDate
                )

                // TODO: Implementar no repository quando backend estiver pronto
                // projectRepository.updateProject(updatedProject)

                // Por enquanto, atualiza localmente
                _uiState.update { it.copy(project = updatedProject) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao atualizar cronograma: ${e.message}")
                }
            }
        }
    }

    fun updateTaskAssignment(taskId: String, newEmployeeId: String, newTeamId: String) {
        viewModelScope.launch {
            try {
                // TODO: Implementar no repository quando backend estiver pronto
                // projectRepository.updateTaskAssignment(taskId, newEmployeeId, newTeamId)

                // Por enquanto, atualiza localmente
                val updatedTasks = _uiState.value.tasks.map { task ->
                    if (task.id == taskId) {
                        val newEmployee = _uiState.value.availableEmployees.find { it.id == newEmployeeId }
                        if (newEmployee != null) {
                            task.copy(
                                assignedTo = task.assignedTo.copy(
                                    id = newEmployee.id,
                                    name = newEmployee.fullName
                                )
                            )
                        } else {
                            task
                        }
                    } else {
                        task
                    }
                }

                _uiState.update { it.copy(tasks = updatedTasks) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Erro ao atualizar atribuição: ${e.message}")
                }
            }
        }
    }
}
