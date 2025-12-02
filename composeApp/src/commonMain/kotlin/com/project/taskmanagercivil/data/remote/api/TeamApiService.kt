package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.team.CreateTeamDto
import com.project.taskmanagercivil.data.remote.dto.team.TeamDto
import com.project.taskmanagercivil.data.remote.dto.team.UpdateTeamDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para operações com Times/Setores
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/teams - Lista todos os times
 * - GET /api/teams/{id} - Busca time por ID
 * - GET /api/teams/department/{department} - Lista times por departamento
 * - GET /api/teams/leader/{leaderId} - Lista times de um líder
 * - POST /api/teams - Cria novo time (requer CRIAR_EQUIPE)
 * - PUT /api/teams/{id} - Atualiza time (requer EDITAR_EQUIPE_PROPRIA ou EDITAR_QUALQUER_EQUIPE)
 * - DELETE /api/teams/{id} - Remove time (requer APAGAR_EQUIPE_PROPRIA ou APAGAR_QUALQUER_EQUIPE)
 *
 * ⚠️ IMPORTANTE: Todas as requisições usam JWT token automaticamente via ApiClient
 * O backend deve validar permissões baseado no token JWT
 *
 * Permissões necessárias (validadas no backend):
 * - ADMIN: Acesso total a todas as operações
 * - GESTOR_OBRAS: Pode criar, editar próprios times, ver todos
 * - LIDER_EQUIPE: Pode editar próprio time (onde é líder), ver todos
 * - FUNCIONARIO: Pode ver todos (read-only)
 */
class TeamApiService {

    /**
     * Lista todos os times
     *
     * Endpoint: GET /api/teams
     * Permissão: Todos podem visualizar (VER_QUALQUER_EQUIPE)
     *
     * @return Lista de TeamDto
     * @throws Exception se houver erro de rede ou autenticação
     */
    suspend fun getAllTeams(): List<TeamDto> {
        val response = ApiClient.httpClient.get("/teams")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar times: ${response.status}")
        }
    }

    /**
     * Busca time por ID
     *
     * Endpoint: GET /api/teams/{id}
     * Permissão: Todos podem visualizar
     *
     * @param id ID do time
     * @return TeamDto
     * @throws Exception se time não encontrado ou erro
     */
    suspend fun getTeamById(id: String): TeamDto {
        val response = ApiClient.httpClient.get("/teams/$id")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else if (response.status == HttpStatusCode.NotFound) {
            throw Exception("Time não encontrado")
        } else {
            throw Exception("Erro ao buscar time: ${response.status}")
        }
    }

    /**
     * Lista times por departamento
     *
     * Endpoint: GET /api/teams/department/{department}
     * Permissão: Todos podem visualizar
     *
     * @param department Departamento (ex: "ARCHITECTURE", "STRUCTURE")
     * @return Lista de TeamDto do departamento
     * @throws Exception se houver erro
     */
    suspend fun getTeamsByDepartment(department: String): List<TeamDto> {
        val response = ApiClient.httpClient.get("/teams/department/$department")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar times do departamento: ${response.status}")
        }
    }

    /**
     * Lista times onde o colaborador é líder
     *
     * Endpoint: GET /api/teams/leader/{leaderId}
     * Permissão: Todos podem visualizar
     *
     * @param leaderId ID do líder/responsável
     * @return Lista de TeamDto onde é líder
     * @throws Exception se houver erro
     */
    suspend fun getTeamsByLeader(leaderId: String): List<TeamDto> {
        val response = ApiClient.httpClient.get("/teams/leader/$leaderId")

        return if (response.status == HttpStatusCode.OK) {
            response.body()
        } else {
            throw Exception("Erro ao buscar times do líder: ${response.status}")
        }
    }

    /**
     * Cria novo time
     *
     * Endpoint: POST /api/teams
     * Permissão necessária: CRIAR_EQUIPE
     * Roles permitidos: ADMIN, GESTOR_OBRAS
     *
     * @param createDto Dados do novo time
     * @return TeamDto criado
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    suspend fun createTeam(createDto: CreateTeamDto): TeamDto {
        val response = ApiClient.httpClient.post("/teams") {
            contentType(ContentType.Application.Json)
            setBody(createDto)
        }

        return when (response.status) {
            HttpStatusCode.Created, HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para criar time")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao criar time: ${response.status}")
        }
    }

    /**
     * Atualiza time existente
     *
     * Endpoint: PUT /api/teams/{id}
     * Permissão necessária:
     * - EDITAR_EQUIPE_PROPRIA (para times que lidera ou gerencia)
     * - EDITAR_QUALQUER_EQUIPE (ADMIN)
     *
     * Roles permitidos:
     * - ADMIN (qualquer time)
     * - GESTOR_OBRAS (times que gerencia)
     * - LIDER_EQUIPE (próprio time onde é líder)
     *
     * @param id ID do time
     * @param updateDto Dados a atualizar (apenas campos preenchidos serão atualizados)
     * @return TeamDto atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateTeam(id: String, updateDto: UpdateTeamDto): TeamDto {
        val response = ApiClient.httpClient.put("/teams/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Time não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar este time")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar time: ${response.status}")
        }
    }

    /**
     * Remove time
     *
     * Endpoint: DELETE /api/teams/{id}
     * Permissão necessária:
     * - APAGAR_EQUIPE_PROPRIA (para times que gerencia)
     * - APAGAR_QUALQUER_EQUIPE (ADMIN)
     *
     * Roles permitidos:
     * - ADMIN (qualquer time)
     * - GESTOR_OBRAS (times que gerencia)
     *
     * ⚠️ ATENÇÃO: Normalmente deve-se marcar como inativo ao invés de deletar
     * Use updateTeam() com isActive=false para soft delete
     *
     * @param id ID do time
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun deleteTeam(id: String) {
        val response = ApiClient.httpClient.delete("/teams/$id")

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.NotFound -> throw Exception("Time não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para remover este time")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao remover time: ${response.status}")
        }
    }

    /**
     * Desativa time (soft delete)
     *
     * Helper method que usa updateTeam() para marcar como inativo
     * Recomendado ao invés de deleteTeam()
     *
     * @param id ID do time
     * @return TeamDto atualizado
     */
    suspend fun deactivateTeam(id: String): TeamDto {
        return updateTeam(
            id = id,
            updateDto = UpdateTeamDto(
                isActive = false
            )
        )
    }

    /**
     * Adiciona membro ao time
     *
     * Helper method para adicionar colaborador ao time
     * Requer primeiro buscar o time atual, adicionar o ID e atualizar
     *
     * @param teamId ID do time
     * @param employeeId ID do colaborador
     * @return TeamDto atualizado
     */
    suspend fun addMember(teamId: String, employeeId: String): TeamDto {
        val currentTeam = getTeamById(teamId)
        val updatedMembers = currentTeam.memberIds + employeeId

        return updateTeam(
            id = teamId,
            updateDto = UpdateTeamDto(
                memberIds = updatedMembers
            )
        )
    }

    /**
     * Remove membro do time
     *
     * Helper method para remover colaborador do time
     *
     * @param teamId ID do time
     * @param employeeId ID do colaborador
     * @return TeamDto atualizado
     */
    suspend fun removeMember(teamId: String, employeeId: String): TeamDto {
        val currentTeam = getTeamById(teamId)
        val updatedMembers = currentTeam.memberIds - employeeId

        return updateTeam(
            id = teamId,
            updateDto = UpdateTeamDto(
                memberIds = updatedMembers
            )
        )
    }

    /**
     * Atualiza líder do time
     *
     * Helper method para trocar o líder/responsável do time
     *
     * @param teamId ID do time
     * @param newLeaderId ID do novo líder (pode ser null para remover líder)
     * @return TeamDto atualizado
     */
    suspend fun updateLeader(teamId: String, newLeaderId: String?): TeamDto {
        return updateTeam(
            id = teamId,
            updateDto = UpdateTeamDto(
                leaderId = newLeaderId
            )
        )
    }
}
