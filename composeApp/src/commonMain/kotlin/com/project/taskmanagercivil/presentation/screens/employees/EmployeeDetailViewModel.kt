package com.project.taskmanagercivil.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class EmployeeDetailUiState(
    val employee: Employee? = null,
    val projects: List<Project> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)


class EmployeeDetailViewModel(
    private val employeeId: String,
    private val employeeRepository: EmployeeRepository,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeDetailUiState())
    val uiState: StateFlow<EmployeeDetailUiState> = _uiState.asStateFlow()

    init {
        loadEmployeeDetails()
    }

    private fun loadEmployeeDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                println("DEBUG: Loading employee with ID: '$employeeId'")
                val employee = employeeRepository.getEmployeeById(employeeId)
                println("DEBUG: Found employee: ${employee?.fullName ?: "NULL"}")

                if (employee != null) {
                    // Buscar os projetos do colaborador
                    val allProjects: List<Project> = projectRepository.getAllProjects()
                    val employeeProjects: List<Project> = allProjects.filter { project: Project ->
                        project.id in employee.projectIds
                    }

                    // Buscar as tarefas do colaborador
                    val allTasks: List<Task> = taskRepository.getAllTasks()
                    val employeeTasks: List<Task> = allTasks.filter { task: Task ->
                        task.assignedTo.id == employeeId
                    }

                    _uiState.update {
                        it.copy(
                            employee = employee,
                            projects = employeeProjects,
                            tasks = employeeTasks,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Colaborador não encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados do colaborador"
                    )
                }
            }
        }
    }

    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            employeeRepository.deleteEmployee(employeeId)
        }
    }

    fun refreshEmployee() {
        loadEmployeeDetails()
    }
}
