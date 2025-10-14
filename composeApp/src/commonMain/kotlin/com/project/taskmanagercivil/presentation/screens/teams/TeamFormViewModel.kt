package com.project.taskmanagercivil.presentation.screens.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TeamRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


data class TeamFormUiState(
    val teamId: String? = null,
    val name: String = "",
    val department: TeamDepartment? = null,
    val description: String = "",
    val leaderId: String? = null,
    val selectedMemberIds: List<String> = emptyList(),
    val selectedProjectIds: List<String> = emptyList(),
    val availableEmployees: List<Employee> = emptyList(),
    val availableProjects: List<Project> = emptyList(),
    val createdDate: kotlinx.datetime.LocalDate? = null,
    val isActive: Boolean = true,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: Map<String, String> = emptyMap()
) {
    val isEditMode: Boolean get() = teamId != null
}


class TeamFormViewModel(
    private val teamId: String?,
    private val teamRepository: TeamRepository,
    private val employeeRepository: EmployeeRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamFormUiState(teamId = teamId))
    val uiState: StateFlow<TeamFormUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                s
                val employees: List<Employee> = employeeRepository.getAllEmployees().first()
                _uiState.update { it.copy(availableEmployees = employees) }

                
                val projects: List<Project> = projectRepository.getAllProjects()
                _uiState.update { it.copy(availableProjects = projects) }

                
                if (teamId != null) {
                    val team = teamRepository.getTeamById(teamId)
                    if (team != null) {
                        _uiState.update {
                            it.copy(
                                name = team.name,
                                department = team.department,
                                description = team.description,
                                leaderId = team.leaderId,
                                selectedMemberIds = team.memberIds,
                                selectedProjectIds = team.projectIds,
                                createdDate = team.createdDate,
                                isActive = team.isActive,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Time não encontrado"
                            )
                        }
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados"
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value) }
        clearFieldError("name")
    }

    fun onDepartmentChange(department: TeamDepartment) {
        _uiState.update { it.copy(department = department) }
        clearFieldError("department")
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onLeaderChange(employeeId: String?) {
        _uiState.update { it.copy(leaderId = employeeId) }
    }

    fun onMemberSelectionChange(employeeId: String, isSelected: Boolean) {
        _uiState.update { currentState ->
            val updatedMembers = if (isSelected) {
                currentState.selectedMemberIds + employeeId
            } else {
                currentState.selectedMemberIds - employeeId
            }
            currentState.copy(selectedMemberIds = updatedMembers)
        }
    }

    fun onProjectSelectionChange(projectId: String, isSelected: Boolean) {
        _uiState.update { currentState ->
            val updatedProjects = if (isSelected) {
                currentState.selectedProjectIds + projectId
            } else {
                currentState.selectedProjectIds - projectId
            }
            currentState.copy(selectedProjectIds = updatedProjects)
        }
    }

    fun onIsActiveChange(isActive: Boolean) {
        _uiState.update { it.copy(isActive = isActive) }
    }

    private fun clearFieldError(field: String) {
        _uiState.update { currentState ->
            currentState.copy(
                validationErrors = currentState.validationErrors - field
            )
        }
    }

    fun saveTeam(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validação
        val errors = mutableMapOf<String, String>()

        if (currentState.name.isBlank()) {
            errors["name"] = "Nome do time é obrigatório"
        }

        if (currentState.department == null) {
            errors["department"] = "Departamento é obrigatório"
        }

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = errors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val team = Team(
                    id = teamId ?: generateId(),
                    name = currentState.name.trim(),
                    department = currentState.department!!,
                    description = currentState.description.trim(),
                    leaderId = currentState.leaderId,
                    memberIds = currentState.selectedMemberIds,
                    projectIds = currentState.selectedProjectIds,
                    createdDate = currentState.createdDate ?: Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
                    isActive = currentState.isActive
                )

                if (teamId != null) {
                    teamRepository.updateTeam(team)
                } else {
                    teamRepository.addTeam(team)
                }

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Erro ao salvar time: ${e.message}"
                    )
                }
            }
        }
    }

    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }
}
