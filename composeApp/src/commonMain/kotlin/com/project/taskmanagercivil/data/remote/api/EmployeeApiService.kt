package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.employee.CreateEmployeeDto
import com.project.taskmanagercivil.data.remote.dto.employee.EmployeeDto
import com.project.taskmanagercivil.data.remote.dto.employee.UpdateEmployeeDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para operações com Colaboradores (Employees)
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/employees - Lista todos os colaboradores
 * - GET /api/employees/{id} - Busca colaborador por ID
 * - GET /api/employees/project/{projectId} - Busca colaboradores por projeto
 * - POST /api/employees - Cria novo colaborador (requer ADICIONAR_FUNCIONARIO)
 * - PUT /api/employees/{id} - Atualiza colaborador (requer EDITAR_FUNCIONARIO)
 * - DELETE /api/employees/{id} - Remove colaborador (requer REMOVER_FUNCIONARIO)
 *
 * ⚠️ IMPORTANTE: Todas as requisições usam JWT token automaticamente via ApiClient
 * O backend deve validar permissões baseado no token JWT
 *
 * Permissões necessárias (validadas no backend):
 * - ADMIN: Acesso total a todas as operações
 * - GESTOR_OBRAS: Pode criar e editar colaboradores
 * - LIDER_EQUIPE: Pode adicionar colaboradores à sua equipe
 * - FUNCIONARIO: Apenas visualização
 */
class EmployeeApiService {

    /**
     * Lista todos os colaboradores
     *
     * Endpoint: GET /api/employees
     * Permissão: Todos podem visualizar (VER_QUALQUER_PROJETO)
     *
     * @return Lista de EmployeeDto
     * @throws Exception se houver erro de rede ou autenticação
     */
    suspend fun getAllEmployees(): List<EmployeeDto> {
        val response = ApiClient.httpClient.get("/employees")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar colaboradores: ${response.status}")
        }
    }

    /**
     * Busca colaborador por ID
     *
     * Endpoint: GET /api/employees/{id}
     * Permissão: Todos podem visualizar
     *
     * @param id ID do colaborador
     * @return EmployeeDto
     * @throws Exception se colaborador não encontrado ou erro
     */
    suspend fun getEmployeeById(id: String): EmployeeDto {
        val response = ApiClient.httpClient.get("/employees/$id")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else if (response.status == HttpStatusCode.NotFound) {
            throw Exception("Colaborador não encontrado")
        } else {
            throw Exception("Erro ao buscar colaborador: ${response.status}")
        }
    }

    /**
     * Busca colaboradores por projeto
     *
     * Endpoint: GET /api/employees?projectId={projectId}
     * Permissão: Todos podem visualizar
     *
     * @param projectId ID do projeto
     * @return Lista de EmployeeDto
     * @throws Exception se houver erro
     */
    suspend fun getEmployeesByProject(projectId: String): List<EmployeeDto> {
        val response = ApiClient.httpClient.get("/employees") {
            url {
                parameters.append("projectId", projectId)
            }
        }

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar colaboradores do projeto: ${response.status}")
        }
    }

    /**
     * Cria novo colaborador
     *
     * Endpoint: POST /api/employees
     * Permissão necessária: ADICIONAR_FUNCIONARIO
     * Roles permitidos: ADMIN, GESTOR_OBRAS
     *
     * @param createDto Dados do novo colaborador
     * @return EmployeeDto criado
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    suspend fun createEmployee(createDto: CreateEmployeeDto): EmployeeDto {
        val response = ApiClient.httpClient.post("/employees") {
            contentType(ContentType.Application.Json)
            setBody(createDto)
        }

        return when (response.status) {
            HttpStatusCode.Created, HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para criar colaborador")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao criar colaborador: ${response.status}")
        }
    }

    /**
     * Atualiza colaborador existente
     *
     * Endpoint: PUT /api/employees/{id}
     * Permissão necessária: EDITAR_FUNCIONARIO
     * Roles permitidos: ADMIN, GESTOR_OBRAS
     *
     * @param id ID do colaborador
     * @param updateDto Dados a atualizar (apenas campos preenchidos serão atualizados)
     * @return EmployeeDto atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateEmployee(id: String, updateDto: UpdateEmployeeDto): EmployeeDto {
        val response = ApiClient.httpClient.put("/employees/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Colaborador não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar colaborador")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar colaborador: ${response.status}")
        }
    }

    /**
     * Remove colaborador
     *
     * Endpoint: DELETE /api/employees/{id}
     * Permissão necessária: REMOVER_FUNCIONARIO
     * Roles permitidos: ADMIN
     *
     * ⚠️ ATENÇÃO: Normalmente deve-se inativar ao invés de deletar
     * Use updateEmployee() com status="INACTIVE" para soft delete
     *
     * @param id ID do colaborador
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun deleteEmployee(id: String) {
        val response = ApiClient.httpClient.delete("/employees/$id")

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.NotFound -> throw Exception("Colaborador não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para remover colaborador")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao remover colaborador: ${response.status}")
        }
    }

    /**
     * Inativa colaborador (soft delete)
     *
     * Helper method que usa updateEmployee() para marcar como inativo
     * Recomendado ao invés de deleteEmployee()
     *
     * @param id ID do colaborador
     * @param terminationDate Data de demissão (ISO 8601: "2024-06-30")
     * @return EmployeeDto atualizado
     */
    suspend fun inactivateEmployee(id: String, terminationDate: String): EmployeeDto {
        return updateEmployee(
            id = id,
            updateDto = UpdateEmployeeDto(
                status = "INACTIVE",
                terminationDate = terminationDate
            )
        )
    }
}
