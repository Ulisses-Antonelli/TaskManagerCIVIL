package com.project.taskmanagercivil.presentation.screens.employees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class EmployeesUiState(
    val employees: List<Employee> = emptyList(),
    val filteredEmployees: List<Employee> = emptyList(),
    val searchQuery: String = "",
    val filterStatus: EmployeeFilterStatus = EmployeeFilterStatus.ALL,
    val sortOrder: EmployeeSortOrder = EmployeeSortOrder.NAME_ASC,
    val isLoading: Boolean = false
)


enum class EmployeeFilterStatus {
    ALL,        // Todos
    ACTIVE,     // Ativos
    INACTIVE    // Demitidos
}


enum class EmployeeSortOrder(val displayName: String) {
    NAME_ASC("Nome (A-Z)"),
    NAME_DESC("Nome (Z-A)"),
    HIRE_DATE_ASC("Admissão (Mais Antigo)"),
    HIRE_DATE_DESC("Admissão (Mais Recente)"),
    ROLE_ASC("Cargo (A-Z)")
}


class EmployeesViewModel(
    private val repository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmployeesUiState())
    val uiState: StateFlow<EmployeesUiState> = _uiState.asStateFlow()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllEmployees().collect { employees ->
                _uiState.update { currentState ->
                    currentState.copy(
                        employees = employees,
                        filteredEmployees = applyFiltersAndSort(
                            employees,
                            currentState.searchQuery,
                            currentState.filterStatus,
                            currentState.sortOrder
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredEmployees = applyFiltersAndSort(
                    currentState.employees,
                    query,
                    currentState.filterStatus,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onFilterStatusChange(status: EmployeeFilterStatus) {
        _uiState.update { currentState ->
            currentState.copy(
                filterStatus = status,
                filteredEmployees = applyFiltersAndSort(
                    currentState.employees,
                    currentState.searchQuery,
                    status,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onSortOrderChange(sortOrder: EmployeeSortOrder) {
        _uiState.update { currentState ->
            currentState.copy(
                sortOrder = sortOrder,
                filteredEmployees = applyFiltersAndSort(
                    currentState.employees,
                    currentState.searchQuery,
                    currentState.filterStatus,
                    sortOrder
                )
            )
        }
    }

    private fun applyFiltersAndSort(
        employees: List<Employee>,
        searchQuery: String,
        filterStatus: EmployeeFilterStatus,
        sortOrder: EmployeeSortOrder
    ): List<Employee> {
        var result = employees

        // Aplicar filtro de status
        result = when (filterStatus) {
            EmployeeFilterStatus.ALL -> result
            EmployeeFilterStatus.ACTIVE -> result.filter { it.isCurrentlyActive() }
            EmployeeFilterStatus.INACTIVE -> result.filter { !it.isCurrentlyActive() }
        }

        // Aplicar busca por texto
        if (searchQuery.isNotBlank()) {
            result = result.filter { employee ->
                employee.fullName.contains(searchQuery, ignoreCase = true) ||
                employee.role.contains(searchQuery, ignoreCase = true) ||
                employee.email.contains(searchQuery, ignoreCase = true)
            }
        }

        // Aplicar ordenação
        result = when (sortOrder) {
            EmployeeSortOrder.NAME_ASC -> result.sortedBy { it.fullName }
            EmployeeSortOrder.NAME_DESC -> result.sortedByDescending { it.fullName }
            EmployeeSortOrder.HIRE_DATE_ASC -> result.sortedBy { it.hireDate }
            EmployeeSortOrder.HIRE_DATE_DESC -> result.sortedByDescending { it.hireDate }
            EmployeeSortOrder.ROLE_ASC -> result.sortedBy { it.role }
        }

        return result
    }

    fun deleteEmployee(employeeId: String) {
        viewModelScope.launch {
            repository.deleteEmployee(employeeId)
        }
    }
}
