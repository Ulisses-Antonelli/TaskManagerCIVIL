package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação do repositório de colaboradores usando dados mockados
 */
class EmployeeRepositoryImpl : EmployeeRepository {
    private val mockData = MockData()
    private val _employees = MutableStateFlow(mockData.employees)
    private val employees: Flow<List<Employee>> = _employees.asStateFlow()

    override fun getAllEmployees(): Flow<List<Employee>> = employees

    override fun getEmployeeById(id: String): Employee? {
        return _employees.value.find { it.id == id }
    }

    override fun getEmployeesByProject(projectId: String): List<Employee> {
        return _employees.value.filter { projectId in it.projectIds }
    }

    override fun addEmployee(employee: Employee) {
        val currentList = _employees.value.toMutableList()
        currentList.add(employee)
        _employees.value = currentList
    }

    override fun updateEmployee(employee: Employee) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == employee.id }
        if (index != -1) {
            currentList[index] = employee
            _employees.value = currentList
        }
    }

    override fun deleteEmployee(id: String) {
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList.removeAt(index)
            _employees.value = currentList
        }
    }
}
