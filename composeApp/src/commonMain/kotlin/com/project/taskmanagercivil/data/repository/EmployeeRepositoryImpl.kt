package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.EmployeeApiService
import com.project.taskmanagercivil.data.remote.dto.employee.CreateEmployeeDto
import com.project.taskmanagercivil.data.remote.dto.employee.EmployeeDto
import com.project.taskmanagercivil.data.remote.dto.employee.UpdateEmployeeDto
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Implementação do repositório de colaboradores usando backend real via EmployeeApiService
 *
 * Integração com Spring Boot backend:
 * - GET /api/employees - Lista todos os colaboradores
 * - GET /api/employees/{id} - Busca colaborador por ID
 * - POST /api/employees - Cria novo colaborador
 * - PUT /api/employees/{id} - Atualiza colaborador
 * - DELETE /api/employees/{id} - Remove colaborador
 *
 * Converte DTOs do backend para modelos de domínio (Employee)
 */
class EmployeeRepositoryImpl(
    private val employeeApiService: EmployeeApiService = EmployeeApiService()
) : EmployeeRepository {

    private val _employees = MutableStateFlow<List<Employee>>(emptyList())
    private val employees: Flow<List<Employee>> = _employees.asStateFlow()

    /**
     * Retorna Flow de colaboradores (atualizado via loadEmployees)
     */
    override fun getAllEmployees(): Flow<List<Employee>> {
        return employees
    }

    /**
     * Carrega colaboradores do backend e atualiza o Flow
     * Deve ser chamado manualmente quando necessário
     */
    suspend fun loadEmployees() {
        try {
            val employeeDtos = employeeApiService.getAllEmployees()
            _employees.value = employeeDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("Erro ao carregar colaboradores: ${e.message}")
            // Mantém lista atual em caso de erro
        }
    }

    override fun getEmployeeById(id: String): Employee? {
        return _employees.value.find { it.id == id }
    }

    /**
     * Busca colaborador por ID no backend
     */
    suspend fun fetchEmployeeById(id: String): Employee? {
        return try {
            val employeeDto = employeeApiService.getEmployeeById(id)
            employeeDto.toDomainModel()
        } catch (e: Exception) {
            println("Erro ao buscar colaborador $id: ${e.message}")
            null
        }
    }

    override fun getEmployeesByProject(projectId: String): List<Employee> {
        return _employees.value.filter { projectId in it.projectIds }
    }

    /**
     * Busca colaboradores por projeto no backend
     */
    suspend fun fetchEmployeesByProject(projectId: String): List<Employee> {
        return try {
            val employeeDtos = employeeApiService.getEmployeesByProject(projectId)
            employeeDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("Erro ao buscar colaboradores do projeto $projectId: ${e.message}")
            emptyList()
        }
    }

    override fun addEmployee(employee: Employee) {
        // Método síncrono mantido para compatibilidade, mas não deve ser usado
        // Use addEmployeeAsync() ao invés
        val currentList = _employees.value.toMutableList()
        currentList.add(employee)
        _employees.value = currentList
    }

    /**
     * Cria novo colaborador no backend
     * Requer permissão: ADICIONAR_FUNCIONARIO (ADMIN, GESTOR_OBRAS)
     */
    suspend fun addEmployeeAsync(employee: Employee): Result<Employee> {
        return try {
            val createDto = employee.toCreateDto()
            val createdDto = employeeApiService.createEmployee(createDto)
            val createdEmployee = createdDto.toDomainModel()

            // Atualiza lista local
            val currentList = _employees.value.toMutableList()
            currentList.add(createdEmployee)
            _employees.value = currentList

            Result.success(createdEmployee)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun updateEmployee(employee: Employee) {
        // Método síncrono mantido para compatibilidade, mas não deve ser usado
        // Use updateEmployeeAsync() ao invés
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == employee.id }
        if (index != -1) {
            currentList[index] = employee
            _employees.value = currentList
        }
    }

    /**
     * Atualiza colaborador no backend
     * Requer permissão: EDITAR_FUNCIONARIO (ADMIN, GESTOR_OBRAS)
     */
    suspend fun updateEmployeeAsync(employee: Employee): Result<Employee> {
        return try {
            val updateDto = employee.toUpdateDto()
            val updatedDto = employeeApiService.updateEmployee(employee.id, updateDto)
            val updatedEmployee = updatedDto.toDomainModel()

            // Atualiza lista local
            val currentList = _employees.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == employee.id }
            if (index != -1) {
                currentList[index] = updatedEmployee
                _employees.value = currentList
            }

            Result.success(updatedEmployee)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun deleteEmployee(id: String) {
        // Método síncrono mantido para compatibilidade, mas não deve ser usado
        // Use deleteEmployeeAsync() ao invés
        val currentList = _employees.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList.removeAt(index)
            _employees.value = currentList
        }
    }

    /**
     * Remove colaborador do backend (hard delete)
     * Requer permissão: REMOVER_FUNCIONARIO (ADMIN)
     *
     * ⚠️ RECOMENDADO: Use inactivateEmployeeAsync() ao invés de deletar
     */
    suspend fun deleteEmployeeAsync(id: String): Result<Unit> {
        return try {
            employeeApiService.deleteEmployee(id)

            // Remove da lista local
            val currentList = _employees.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList.removeAt(index)
                _employees.value = currentList
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inativa colaborador (soft delete)
     * Recomendado ao invés de deleteEmployee()
     */
    suspend fun inactivateEmployeeAsync(id: String, terminationDate: LocalDate): Result<Employee> {
        return try {
            val terminationDateStr = terminationDate.toString() // ISO 8601: "2024-06-30"
            val updatedDto = employeeApiService.inactivateEmployee(id, terminationDateStr)
            val updatedEmployee = updatedDto.toDomainModel()

            // Atualiza lista local
            val currentList = _employees.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = updatedEmployee
                _employees.value = currentList
            }

            Result.success(updatedEmployee)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Converte EmployeeDto para Employee (domain model)
     */
    private fun EmployeeDto.toDomainModel(): Employee {
        return Employee(
            id = id,
            fullName = fullName,
            role = role,
            email = email,
            phone = phone,
            cpf = cpf,
            hireDate = LocalDate.parse(hireDate),
            terminationDate = terminationDate?.let { LocalDate.parse(it) },
            avatarUrl = avatarUrl,
            projectIds = projectIds,
            isActive = status == "ACTIVE"
        )
    }

    /**
     * Converte Employee para CreateEmployeeDto
     */
    private fun Employee.toCreateDto(): CreateEmployeeDto {
        return CreateEmployeeDto(
            userId = null,
            fullName = fullName,
            email = email,
            phone = phone,
            cpf = cpf,
            role = role,
            department = null, // TODO: Adicionar campo department ao Employee
            hireDate = hireDate.toString(),
            projectIds = projectIds,
            teamIds = emptyList(), // TODO: Adicionar campo teamIds ao Employee
            avatarUrl = avatarUrl
        )
    }

    /**
     * Converte Employee para UpdateEmployeeDto
     */
    private fun Employee.toUpdateDto(): UpdateEmployeeDto {
        return UpdateEmployeeDto(
            fullName = fullName,
            email = email,
            phone = phone,
            cpf = cpf,
            role = role,
            terminationDate = terminationDate?.toString(),
            projectIds = projectIds,
            status = if (isActive) "ACTIVE" else "INACTIVE",
            avatarUrl = avatarUrl
        )
    }
}
