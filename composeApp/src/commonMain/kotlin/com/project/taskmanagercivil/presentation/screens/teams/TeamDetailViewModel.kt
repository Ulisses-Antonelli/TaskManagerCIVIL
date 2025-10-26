package com.project.taskmanagercivil.presentation.screens.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TaskRepository
import com.project.taskmanagercivil.domain.repository.TeamRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class TeamDetailUiState(
    val team: Team? = null,
    val members: List<Employee> = emptyList(),
    val projects: List<Project> = emptyList(),
    val leader: Employee? = null,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)


class TeamDetailViewModel(
    private val teamId: String,
    private val teamRepository: TeamRepository,
    private val employeeRepository: EmployeeRepository,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeamDetailUiState())
    val uiState: StateFlow<TeamDetailUiState> = _uiState.asStateFlow()

    init {
        loadTeamDetails()
    }

    private fun loadTeamDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val team = teamRepository.getTeamById(teamId)

                if (team != null) {
                    
                    val allProjects: List<Project> = projectRepository.getAllProjects()
                    val teamProjects: List<Project> = allProjects.filter { project: Project ->
                        project.id in team.projectIds
                    }

                   
                    val allEmployees: List<Employee> = employeeRepository.getAllEmployees().first()
                    val teamMembers: List<Employee> = allEmployees.filter { employee: Employee ->
                        employee.id in team.memberIds
                    }

 
                    val teamLeader: Employee? = if (team.leaderId != null) {
                        employeeRepository.getEmployeeById(team.leaderId)
                    } else {
                        null
                    }

                    // Buscar as tarefas dos membros do time
                    val allTasks: List<Task> = taskRepository.getAllTasks()
                    val teamTasks: List<Task> = allTasks.filter { task: Task ->
                        task.assignedTo.id in team.memberIds
                    }

                    _uiState.update {
                        it.copy(
                            team = team,
                            members = teamMembers,
                            projects = teamProjects,
                            leader = teamLeader,
                            tasks = teamTasks,
                            isLoading = false,
                            errorMessage = null
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
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados do time"
                    )
                }
            }
        }
    }

    fun deleteTeam(teamId: String) {
        viewModelScope.launch {
            teamRepository.deleteTeam(teamId)
        }
    }

    fun refreshTeam() {
        loadTeamDetails()
    }
}
