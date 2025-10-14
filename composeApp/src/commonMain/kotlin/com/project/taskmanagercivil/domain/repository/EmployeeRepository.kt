package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.Employee
import kotlinx.coroutines.flow.Flow

/**
 * Interface de repositório para operações CRUD de colaboradores
 */
interface EmployeeRepository {
    fun getAllEmployees(): Flow<List<Employee>>
    fun getEmployeeById(id: String): Employee?
    fun getEmployeesByProject(projectId: String): List<Employee>
    fun addEmployee(employee: Employee)
    fun updateEmployee(employee: Employee)
    fun deleteEmployee(id: String)
}
