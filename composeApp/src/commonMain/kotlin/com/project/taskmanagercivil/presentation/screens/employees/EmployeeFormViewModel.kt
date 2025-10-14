package com.project.taskmanagercivil.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate


data class EmployeeFormUiState(
    val employeeId: String? = null,
    val fullName: String = "",
    val role: String = "",
    val email: String = "",
    val phone: String = "",
    val cpf: String = "",
    val hireDate: LocalDate? = null,
    val terminationDate: LocalDate? = null,
    val selectedProjectIds: List<String> = emptyList(),
    val isActive: Boolean = true,
    val availableProjects: List<Project> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: Map<String, String> = emptyMap()
) {
    val isEditMode: Boolean get() = employeeId != null
}


class EmployeeFormViewModel(
    private val employeeId: String?,
    private val employeeRepository: EmployeeRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeeFormUiState(employeeId = employeeId))
    val uiState: StateFlow<EmployeeFormUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                s
                val projects: List<Project> = projectRepository.getAllProjects()
                _uiState.update { it.copy(availableProjects = projects) }

                if (employeeId != null) {
                    val employee = employeeRepository.getEmployeeById(employeeId)
                    if (employee != null) {
                        _uiState.update {
                            it.copy(
                                fullName = employee.fullName,
                                role = employee.role,
                                email = employee.email,
                                phone = employee.phone ?: "",
                                cpf = employee.cpf ?: "",
                                hireDate = employee.hireDate,
                                terminationDate = employee.terminationDate,
                                selectedProjectIds = employee.projectIds,
                                isActive = employee.isActive,
                                isLoading = false
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

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value) }
        clearFieldError("fullName")
    }

    fun onRoleChange(value: String) {
        _uiState.update { it.copy(role = value) }
        clearFieldError("role")
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
        clearFieldError("email")
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value) }
    }

    fun onCpfChange(value: String) {
        _uiState.update { it.copy(cpf = value) }
    }

    fun onHireDateChange(date: LocalDate?) {
        _uiState.update { it.copy(hireDate = date) }
        clearFieldError("hireDate")
    }

    fun onTerminationDateChange(date: LocalDate?) {
        _uiState.update { it.copy(terminationDate = date) }
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

    fun saveEmployee(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        // Validação
        val errors = mutableMapOf<String, String>()

        if (currentState.fullName.isBlank()) {
            errors["fullName"] = "Nome completo é obrigatório"
        }

        if (currentState.role.isBlank()) {
            errors["role"] = "Cargo/Função é obrigatório"
        }

        if (currentState.email.isBlank()) {
            errors["email"] = "Email é obrigatório"
        } else if (!isValidEmail(currentState.email)) {
            errors["email"] = "Email inválido"
        }

        if (currentState.hireDate == null) {
            errors["hireDate"] = "Data de admissão é obrigatória"
        }

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = errors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val employee = Employee(
                    id = employeeId ?: generateId(),
                    fullName = currentState.fullName.trim(),
                    role = currentState.role.trim(),
                    email = currentState.email.trim(),
                    phone = currentState.phone.trim().ifBlank { null },
                    cpf = currentState.cpf.trim().ifBlank { null },
                    hireDate = currentState.hireDate!!,
                    terminationDate = currentState.terminationDate,
                    projectIds = currentState.selectedProjectIds,
                    isActive = currentState.isActive
                )

                if (employeeId != null) {
                    employeeRepository.updateEmployee(employee)
                } else {
                    employeeRepository.addEmployee(employee)
                }

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Erro ao salvar colaborador: ${e.message}"
                    )
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun generateId(): String {
        return kotlinx.datetime.Clock.System.now().toEpochMilliseconds().toString()
    }
}
