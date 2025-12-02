package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.project.CreateProjectDto
import com.project.taskmanagercivil.data.remote.dto.project.ProjectDto
import com.project.taskmanagercivil.data.remote.dto.project.UpdateProjectDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para operações com Projetos/Obras
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/projects - Lista todos os projetos
 * - GET /api/projects/{id} - Busca projeto por ID
 * - POST /api/projects - Cria novo projeto (requer CRIAR_PROJETO)
 * - PUT /api/projects/{id} - Atualiza projeto (requer EDITAR_PROJETO_PROPRIO ou EDITAR_QUALQUER_PROJETO)
 * - DELETE /api/projects/{id} - Remove projeto (requer APAGAR_PROJETO_PROPRIO ou APAGAR_QUALQUER_PROJETO)
 *
 * ⚠️ IMPORTANTE: Todas as requisições usam JWT token automaticamente via ApiClient
 * O backend deve validar permissões baseado no token JWT
 *
 * Permissões necessárias (validadas no backend):
 * - ADMIN: Acesso total a todas as operações
 * - GESTOR_OBRAS: Pode criar, editar/apagar próprios projetos, ver todos
 * - LIDER_EQUIPE: Pode ver todos (read-only)
 * - FUNCIONARIO: Pode ver todos (read-only)
 */
class ProjectApiService {

    /**
     * Lista todos os projetos
     *
     * Endpoint: GET /api/projects
     * Permissão: Todos podem visualizar (VER_QUALQUER_PROJETO)
     *
     * @return Lista de ProjectDto
     * @throws Exception se houver erro de rede ou autenticação
     */
    suspend fun getAllProjects(): List<ProjectDto> {
        val response = ApiClient.httpClient.get("/projects")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar projetos: ${response.status}")
        }
    }

    /**
     * Busca projeto por ID
     *
     * Endpoint: GET /api/projects/{id}
     * Permissão: Todos podem visualizar
     *
     * @param id ID do projeto
     * @return ProjectDto
     * @throws Exception se projeto não encontrado ou erro
     */
    suspend fun getProjectById(id: String): ProjectDto {
        val response = ApiClient.httpClient.get("/projects/$id")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else if (response.status == HttpStatusCode.NotFound) {
            throw Exception("Projeto não encontrado")
        } else {
            throw Exception("Erro ao buscar projeto: ${response.status}")
        }
    }

    /**
     * Cria novo projeto
     *
     * Endpoint: POST /api/projects
     * Permissão necessária: CRIAR_PROJETO
     * Roles permitidos: ADMIN, GESTOR_OBRAS
     *
     * @param createDto Dados do novo projeto
     * @return ProjectDto criado
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    suspend fun createProject(createDto: CreateProjectDto): ProjectDto {
        val response = ApiClient.httpClient.post("/projects") {
            contentType(ContentType.Application.Json)
            setBody(createDto)
        }

        return when (response.status) {
            HttpStatusCode.Created, HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para criar projeto")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao criar projeto: ${response.status}")
        }
    }

    /**
     * Atualiza projeto existente
     *
     * Endpoint: PUT /api/projects/{id}
     * Permissão necessária: EDITAR_PROJETO_PROPRIO (para projetos que gerencia) ou EDITAR_QUALQUER_PROJETO (ADMIN)
     * Roles permitidos: ADMIN (qualquer projeto), GESTOR_OBRAS (próprios projetos)
     *
     * @param id ID do projeto
     * @param updateDto Dados a atualizar (apenas campos preenchidos serão atualizados)
     * @return ProjectDto atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateProject(id: String, updateDto: UpdateProjectDto): ProjectDto {
        val response = ApiClient.httpClient.put("/projects/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Projeto não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar este projeto")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar projeto: ${response.status}")
        }
    }

    /**
     * Remove projeto
     *
     * Endpoint: DELETE /api/projects/{id}
     * Permissão necessária: APAGAR_PROJETO_PROPRIO (para projetos que gerencia) ou APAGAR_QUALQUER_PROJETO (ADMIN)
     * Roles permitidos: ADMIN (qualquer projeto), GESTOR_OBRAS (próprios projetos)
     *
     * ⚠️ ATENÇÃO: Normalmente deve-se marcar como CANCELLED ao invés de deletar
     * Use updateProject() com status="CANCELLED" para soft delete
     *
     * @param id ID do projeto
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun deleteProject(id: String) {
        val response = ApiClient.httpClient.delete("/projects/$id")

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.NotFound -> throw Exception("Projeto não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para remover este projeto")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao remover projeto: ${response.status}")
        }
    }

    /**
     * Cancela projeto (soft delete)
     *
     * Helper method que usa updateProject() para marcar como cancelado
     * Recomendado ao invés de deleteProject()
     *
     * @param id ID do projeto
     * @return ProjectDto atualizado
     */
    suspend fun cancelProject(id: String): ProjectDto {
        return updateProject(
            id = id,
            updateDto = UpdateProjectDto(
                status = "CANCELLED"
            )
        )
    }

    /**
     * Atualiza progresso do projeto
     *
     * Helper method para atualizar apenas o progresso
     *
     * @param id ID do projeto
     * @param progress Progresso (0.0 a 100.0)
     * @return ProjectDto atualizado
     */
    suspend fun updateProgress(id: String, progress: Double): ProjectDto {
        return updateProject(
            id = id,
            updateDto = UpdateProjectDto(
                progress = progress
            )
        )
    }
}
