package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.TeamApiService
import com.project.taskmanagercivil.data.remote.dto.team.CreateTeamDto
import com.project.taskmanagercivil.data.remote.dto.team.TeamDto
import com.project.taskmanagercivil.data.remote.dto.team.UpdateTeamDto
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.domain.repository.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate

/**
 * Implementação do repositório de times com integração ao backend
 *
 * Esta implementação se conecta ao backend via TeamApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ IMPORTANTE: Métodos async lançam exceções em caso de erro
 * As ViewModels devem tratar os erros usando try-catch
 */
class TeamRepositoryImpl(
    private val teamApiService: TeamApiService = TeamApiService()
) : TeamRepository {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    private val teams: Flow<List<Team>> = _teams.asStateFlow()

    /**
     * Carrega times do backend e atualiza o estado local
     *
     * Este método deve ser chamado pelo ViewModel no init ou quando necessário refresh
     *
     * @throws Exception se houver erro de rede ou autenticação
     */
    suspend fun loadTeams() {
        try {
            val teamDtos = teamApiService.getAllTeams()
            _teams.value = teamDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao carregar times: ${e.message}")
            throw e
        }
    }

    /**
     * Retorna Flow de times (dados do estado local)
     *
     * ⚠️ IMPORTANTE: Chame loadTeams() antes para garantir dados atualizados
     *
     * @return Flow<List<Team>> com dados do cache local
     */
    override fun getAllTeams(): Flow<List<Team>> = teams

    /**
     * Busca time por ID (do cache local)
     *
     * @param id ID do time
     * @return Team ou null se não encontrado no cache
     */
    override fun getTeamById(id: String): Team? {
        return _teams.value.find { it.id == id }
    }

    /**
     * Busca times por projeto (do cache local)
     *
     * @param projectId ID do projeto
     * @return Lista de teams que trabalham no projeto
     */
    override fun getTeamsByProject(projectId: String): List<Team> {
        return _teams.value.filter { projectId in it.projectIds }
    }

    /**
     * Busca times por departamento (do cache local)
     *
     * @param department Nome do departamento (ex: "ARCHITECTURE")
     * @return Lista de teams do departamento
     */
    override fun getTeamsByDepartment(department: String): List<Team> {
        return _teams.value.filter { it.department.name == department }
    }

    /**
     * Busca time por ID do backend (sempre dados frescos)
     *
     * Diferente de getTeamById(), este método busca direto do backend
     *
     * @param id ID do time
     * @return Team ou null se não encontrado
     * @throws Exception se houver erro de rede
     */
    suspend fun getTeamByIdAsync(id: String): Team? {
        return try {
            val teamDto = teamApiService.getTeamById(id)
            teamDto.toDomainModel()
        } catch (e: Exception) {
            if (e.message?.contains("não encontrado") == true) {
                null
            } else {
                println("❌ Erro ao buscar time $id: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Busca times por líder do backend
     *
     * @param leaderId ID do líder
     * @return Lista de teams onde é líder
     * @throws Exception se houver erro
     */
    suspend fun getTeamsByLeaderAsync(leaderId: String): List<Team> {
        return try {
            val teamDtos = teamApiService.getTeamsByLeader(leaderId)
            teamDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao buscar times do líder $leaderId: ${e.message}")
            throw e
        }
    }

    /**
     * Adiciona novo time
     *
     * Requer permissão: CRIAR_EQUIPE (ADMIN, GESTOR_OBRAS)
     *
     * @param team Dados do time a criar
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    override fun addTeam(team: Team) {
        // Método síncrono legado - use addTeamAsync() ao invés
        val currentList = _teams.value.toMutableList()
        currentList.add(team)
        _teams.value = currentList
    }

    /**
     * Adiciona novo time (versão async que integra com backend)
     *
     * Requer permissão: CRIAR_EQUIPE (ADMIN, GESTOR_OBRAS)
     *
     * @param team Dados do time a criar
     * @return Team criado com ID do backend
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    suspend fun addTeamAsync(team: Team): Team {
        return try {
            val createDto = team.toCreateDto()
            val createdDto = teamApiService.createTeam(createDto)
            val createdTeam = createdDto.toDomainModel()

            // Atualiza cache local
            val currentList = _teams.value.toMutableList()
            currentList.add(createdTeam)
            _teams.value = currentList

            createdTeam
        } catch (e: Exception) {
            println("❌ Erro ao criar time: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza time existente
     *
     * Requer permissão: EDITAR_EQUIPE_PROPRIA ou EDITAR_QUALQUER_EQUIPE
     *
     * @param team Dados do time a atualizar
     */
    override fun updateTeam(team: Team) {
        // Método síncrono legado - use updateTeamAsync() ao invés
        val currentList = _teams.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == team.id }
        if (index != -1) {
            currentList[index] = team
            _teams.value = currentList
        }
    }

    /**
     * Atualiza time existente (versão async que integra com backend)
     *
     * Requer permissão: EDITAR_EQUIPE_PROPRIA ou EDITAR_QUALQUER_EQUIPE
     *
     * @param team Dados do time a atualizar
     * @return Team atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateTeamAsync(team: Team): Team {
        return try {
            val updateDto = team.toUpdateDto()
            val updatedDto = teamApiService.updateTeam(team.id, updateDto)
            val updatedTeam = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _teams.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == team.id }
            if (index != -1) {
                currentList[index] = updatedTeam
                _teams.value = currentList
            }

            updatedTeam
        } catch (e: Exception) {
            println("❌ Erro ao atualizar time ${team.id}: ${e.message}")
            throw e
        }
    }

    /**
     * Remove time
     *
     * Requer permissão: APAGAR_EQUIPE_PROPRIA ou APAGAR_QUALQUER_EQUIPE
     *
     * @param id ID do time a remover
     */
    override fun deleteTeam(id: String) {
        // Método síncrono legado - use deleteTeamAsync() ao invés
        val currentList = _teams.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList.removeAt(index)
            _teams.value = currentList
        }
    }

    /**
     * Remove time (versão async que integra com backend)
     *
     * Requer permissão: APAGAR_EQUIPE_PROPRIA ou APAGAR_QUALQUER_EQUIPE
     *
     * ⚠️ RECOMENDAÇÃO: Usar deactivateTeamAsync() ao invés para soft delete
     *
     * @param id ID do time a remover
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun deleteTeamAsync(id: String) {
        try {
            teamApiService.deleteTeam(id)

            // Remove do cache local
            val currentList = _teams.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList.removeAt(index)
                _teams.value = currentList
            }
        } catch (e: Exception) {
            println("❌ Erro ao remover time $id: ${e.message}")
            throw e
        }
    }

    /**
     * Desativa time (soft delete recomendado)
     *
     * Marca o time como inativo ao invés de deletar
     *
     * @param id ID do time a desativar
     * @return Team com isActive=false
     * @throws Exception se sem permissão ou erro
     */
    suspend fun deactivateTeamAsync(id: String): Team {
        return try {
            val deactivatedDto = teamApiService.deactivateTeam(id)
            val deactivatedTeam = deactivatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _teams.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = deactivatedTeam
                _teams.value = currentList
            }

            deactivatedTeam
        } catch (e: Exception) {
            println("❌ Erro ao desativar time $id: ${e.message}")
            throw e
        }
    }

    /**
     * Adiciona membro ao time
     *
     * @param teamId ID do time
     * @param employeeId ID do colaborador
     * @return Team atualizado
     * @throws Exception se houver erro
     */
    suspend fun addMemberAsync(teamId: String, employeeId: String): Team {
        return try {
            val updatedDto = teamApiService.addMember(teamId, employeeId)
            val updatedTeam = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _teams.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == teamId }
            if (index != -1) {
                currentList[index] = updatedTeam
                _teams.value = currentList
            }

            updatedTeam
        } catch (e: Exception) {
            println("❌ Erro ao adicionar membro ao time $teamId: ${e.message}")
            throw e
        }
    }

    /**
     * Remove membro do time
     *
     * @param teamId ID do time
     * @param employeeId ID do colaborador
     * @return Team atualizado
     * @throws Exception se houver erro
     */
    suspend fun removeMemberAsync(teamId: String, employeeId: String): Team {
        return try {
            val updatedDto = teamApiService.removeMember(teamId, employeeId)
            val updatedTeam = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _teams.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == teamId }
            if (index != -1) {
                currentList[index] = updatedTeam
                _teams.value = currentList
            }

            updatedTeam
        } catch (e: Exception) {
            println("❌ Erro ao remover membro do time $teamId: ${e.message}")
            throw e
        }
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    /**
     * Converte TeamDto (backend) para Team (domain model)
     */
    private fun TeamDto.toDomainModel(): Team {
        return Team(
            id = this.id,
            name = this.name,
            department = TeamDepartment.valueOf(this.department),
            description = this.description,
            leaderId = this.leaderId,
            memberIds = this.memberIds,
            projectIds = this.projectIds,
            createdDate = LocalDate.parse(this.createdDate),
            isActive = this.isActive
        )
    }

    /**
     * Converte Team (domain) para CreateTeamDto (backend)
     */
    private fun Team.toCreateDto(): CreateTeamDto {
        return CreateTeamDto(
            name = this.name,
            department = this.department.name,
            description = this.description,
            leaderId = this.leaderId,
            memberIds = this.memberIds,
            projectIds = this.projectIds,
            isActive = this.isActive
        )
    }

    /**
     * Converte Team (domain) para UpdateTeamDto (backend)
     */
    private fun Team.toUpdateDto(): UpdateTeamDto {
        return UpdateTeamDto(
            name = this.name,
            department = this.department.name,
            description = this.description,
            leaderId = this.leaderId,
            memberIds = this.memberIds,
            projectIds = this.projectIds,
            isActive = this.isActive
        )
    }
}
