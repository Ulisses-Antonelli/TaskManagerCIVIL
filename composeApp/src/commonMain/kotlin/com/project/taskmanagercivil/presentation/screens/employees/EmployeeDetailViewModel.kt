package com.project.taskmanagercivil.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class EmployeeDetailUiState(
    val employee: Employee? = null,
    val projects: List<Project> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)


class EmployeeDetailViewModel(
    private val employeeId: String,
    private val employeeRepository: EmployeeRepository,
    private val projectRepository: ProjectRepository
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
                val employee = employeeRepository.getEmployeeById(employeeId)

                if (employee != null) {
                    // Buscar os projetos do colaborador
                    val allProjects: List<Project> = projectRepository.getAllProjects()
                    val employeeProjects: List<Project> = allProjects.filter { project: Project ->
                        project.id in employee.projectIds
                    }

                    _uiState.update {
                        it.copy(
                            employee = employee,
                            projects = employeeProjects,
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
