package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.task.CreateTaskDto
import com.project.taskmanagercivil.data.remote.dto.task.TaskDto
import com.project.taskmanagercivil.data.remote.dto.task.UpdateTaskDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para operações com Tarefas
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/tasks - Lista todas as tarefas
 * - GET /api/tasks/{id} - Busca tarefa por ID
 * - GET /api/tasks/project/{projectId} - Lista tarefas de um projeto
 * - GET /api/tasks/assigned/{userId} - Lista tarefas atribuídas a um usuário
 * - POST /api/tasks - Cria nova tarefa
 * - PUT /api/tasks/{id} - Atualiza tarefa
 * - DELETE /api/tasks/{id} - Remove tarefa
 * - PUT /api/tasks/{id}/approve - Aprova tarefa (GESTOR/LIDER)
 *
 * ⚠️ IMPORTANTE: Todas as requisições usam JWT token automaticamente via ApiClient
 * O backend deve validar permissões baseado no token JWT
 *
 * Permissões necessárias (validadas no backend):
 * - ADMIN: Acesso total a todas as operações
 * - GESTOR_OBRAS: Pode criar, editar qualquer, aprovar, ver todas
 * - LIDER_EQUIPE: Pode criar, editar próprias tarefas, aprovar tarefas da equipe, ver todas
 * - FUNCIONARIO: Pode editar tarefas atribuídas (status, progresso), ver todas
 */
class TaskApiService {

    /**
     * Lista todas as tarefas
     *
     * Endpoint: GET /api/tasks
     * Permissão: Todos podem visualizar (VER_QUALQUER_TAREFA)
     *
     * @return Lista de TaskDto
     * @throws Exception se houver erro de rede ou autenticação
     */
    suspend fun getAllTasks(): List<TaskDto> {
        val response = ApiClient.httpClient.get("/tasks")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar tarefas: ${response.status}")
        }
    }

    /**
     * Busca tarefa por ID
     *
     * Endpoint: GET /api/tasks/{id}
     * Permissão: Todos podem visualizar
     *
     * @param id ID da tarefa
     * @return TaskDto
     * @throws Exception se tarefa não encontrada ou erro
     */
    suspend fun getTaskById(id: String): TaskDto {
        val response = ApiClient.httpClient.get("/tasks/$id")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else if (response.status == HttpStatusCode.NotFound) {
            throw Exception("Tarefa não encontrada")
        } else {
            throw Exception("Erro ao buscar tarefa: ${response.status}")
        }
    }

    /**
     * Lista tarefas de um projeto
     *
     * Endpoint: GET /api/tasks/project/{projectId}
     * Permissão: Todos podem visualizar
     *
     * @param projectId ID do projeto
     * @return Lista de TaskDto do projeto
     * @throws Exception se houver erro
     */
    suspend fun getTasksByProject(projectId: String): List<TaskDto> {
        val response = ApiClient.httpClient.get("/tasks/project/$projectId")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar tarefas do projeto: ${response.status}")
        }
    }

    /**
     * Lista tarefas atribuídas a um usuário
     *
     * Endpoint: GET /api/tasks/assigned/{userId}
     * Permissão: Todos podem visualizar
     *
     * @param userId ID do usuário responsável
     * @return Lista de TaskDto atribuídas ao usuário
     * @throws Exception se houver erro
     */
    suspend fun getTasksByAssignedUser(userId: String): List<TaskDto> {
        val response = ApiClient.httpClient.get("/tasks/assigned/$userId")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar tarefas do usuário: ${response.status}")
        }
    }

    /**
     * Cria nova tarefa
     *
     * Endpoint: POST /api/tasks
     * Permissão necessária: CRIAR_TAREFA
     * Roles permitidos: ADMIN, GESTOR_OBRAS, LIDER_EQUIPE
     *
     * @param createDto Dados da nova tarefa
     * @return TaskDto criado
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    suspend fun createTask(createDto: CreateTaskDto): TaskDto {
        val response = ApiClient.httpClient.post("/tasks") {
            contentType(ContentType.Application.Json)
            setBody(createDto)
        }

        return when (response.status) {
            HttpStatusCode.Created, HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para criar tarefa")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao criar tarefa: ${response.status}")
        }
    }

    /**
     * Atualiza tarefa existente
     *
     * Endpoint: PUT /api/tasks/{id}
     * Permissão necessária:
     * - EDITAR_TAREFA_ATRIBUIDA (para tarefas atribuídas ao usuário - FUNCIONARIO)
     * - EDITAR_TAREFA_PROPRIA (para tarefas criadas pelo usuário - LIDER_EQUIPE)
     * - EDITAR_QUALQUER_TAREFA (ADMIN, GESTOR_OBRAS)
     *
     * Roles permitidos:
     * - ADMIN (qualquer tarefa)
     * - GESTOR_OBRAS (qualquer tarefa)
     * - LIDER_EQUIPE (tarefas próprias)
     * - FUNCIONARIO (tarefas atribuídas, apenas status e progresso)
     *
     * @param id ID da tarefa
     * @param updateDto Dados a atualizar (apenas campos preenchidos serão atualizados)
     * @return TaskDto atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateTask(id: String, updateDto: UpdateTaskDto): TaskDto {
        val response = ApiClient.httpClient.put("/tasks/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Tarefa não encontrada")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar esta tarefa")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar tarefa: ${response.status}")
        }
    }

    /**
     * Remove tarefa
     *
     * Endpoint: DELETE /api/tasks/{id}
     * Permissão necessária:
     * - APAGAR_TAREFA_PROPRIA (para tarefas criadas pelo usuário)
     * - APAGAR_QUALQUER_TAREFA (ADMIN, GESTOR_OBRAS)
     *
     * Roles permitidos:
     * - ADMIN (qualquer tarefa)
     * - GESTOR_OBRAS (qualquer tarefa)
     * - LIDER_EQUIPE (tarefas próprias)
     *
     * ⚠️ ATENÇÃO: Normalmente deve-se marcar como INATIVA ao invés de deletar
     * Use updateTask() com status="INATIVA" para soft delete
     *
     * @param id ID da tarefa
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun deleteTask(id: String) {
        val response = ApiClient.httpClient.delete("/tasks/$id")

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.NotFound -> throw Exception("Tarefa não encontrada")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para remover esta tarefa")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao remover tarefa: ${response.status}")
        }
    }

    /**
     * Marca tarefa como inativa (soft delete)
     *
     * Helper method que usa updateTask() para marcar como inativa
     * Recomendado ao invés de deleteTask()
     *
     * @param id ID da tarefa
     * @return TaskDto atualizado
     */
    suspend fun inactivateTask(id: String): TaskDto {
        return updateTask(
            id = id,
            updateDto = UpdateTaskDto(
                status = "INATIVA"
            )
        )
    }

    /**
     * Aprova tarefa
     *
     * Endpoint: PUT /api/tasks/{id}/approve
     * Permissão necessária: APROVAR_TAREFA_EQUIPE (LIDER_EQUIPE) ou APROVAR_QUALQUER_TAREFA (ADMIN, GESTOR_OBRAS)
     *
     * @param id ID da tarefa
     * @param aprovadoPor Nome do aprovador
     * @return TaskDto aprovado
     * @throws Exception se sem permissão (403) ou erro
     */
    suspend fun approveTask(id: String, aprovadoPor: String): TaskDto {
        val response = ApiClient.httpClient.put("/tasks/$id/approve") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("aprovadoPor" to aprovadoPor))
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Tarefa não encontrada")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para aprovar esta tarefa")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao aprovar tarefa: ${response.status}")
        }
    }

    /**
     * Atualiza apenas o progresso da tarefa
     *
     * Helper method para atualizar progresso sem precisar enviar todos os campos
     *
     * @param id ID da tarefa
     * @param progress Progresso (0.0 a 1.0)
     * @return TaskDto atualizado
     */
    suspend fun updateProgress(id: String, progress: Float): TaskDto {
        return updateTask(
            id = id,
            updateDto = UpdateTaskDto(
                progress = progress
            )
        )
    }

    /**
     * Atualiza apenas o status da tarefa
     *
     * Helper method para atualizar status sem precisar enviar todos os campos
     *
     * @param id ID da tarefa
     * @param status Novo status (ex: "IN_PROGRESS", "COMPLETED")
     * @return TaskDto atualizado
     */
    suspend fun updateStatus(id: String, status: String): TaskDto {
        return updateTask(
            id = id,
            updateDto = UpdateTaskDto(
                status = status
            )
        )
    }

    /**
     * Reatribui tarefa para outro usuário
     *
     * Helper method para reatribuir tarefa
     *
     * @param id ID da tarefa
     * @param newAssignedToId ID do novo responsável
     * @return TaskDto atualizado
     */
    suspend fun reassignTask(id: String, newAssignedToId: String): TaskDto {
        return updateTask(
            id = id,
            updateDto = UpdateTaskDto(
                assignedToId = newAssignedToId
            )
        )
    }
}
