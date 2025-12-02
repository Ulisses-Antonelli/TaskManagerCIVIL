package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.ProjectApiService
import com.project.taskmanagercivil.data.remote.dto.project.CreateProjectDto
import com.project.taskmanagercivil.data.remote.dto.project.ProjectDto
import com.project.taskmanagercivil.data.remote.dto.project.UpdateProjectDto
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.datetime.LocalDate

/**
 * Implementação do repositório de projetos com integração ao backend
 *
 * Esta implementação se conecta ao backend via ProjectApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ IMPORTANTE: Todos os métodos lançam exceções em caso de erro
 * As ViewModels devem tratar os erros usando try-catch
 */
class ProjectRepositoryImpl(
    private val projectApiService: ProjectApiService = ProjectApiService()
) : ProjectRepository {

    /**
     * Lista todos os projetos do backend
     *
     * @return Lista de projetos convertidos para domain model
     * @throws Exception se houver erro de rede ou autenticação
     */
    override suspend fun getAllProjects(): List<Project> {
        return try {
            val projectDtos = projectApiService.getAllProjects()
            projectDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao buscar projetos: ${e.message}")
            throw e
        }
    }

    /**
     * Busca projeto por ID
     *
     * @param id ID do projeto
     * @return Project ou null se não encontrado
     * @throws Exception se houver erro de rede ou autenticação
     */
    override suspend fun getProjectById(id: String): Project? {
        return try {
            val projectDto = projectApiService.getProjectById(id)
            projectDto.toDomainModel()
        } catch (e: Exception) {
            if (e.message?.contains("não encontrado") == true) {
                null
            } else {
                println("❌ Erro ao buscar projeto $id: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Busca tarefas de um projeto
     *
     * ⚠️ NOTA: Este método será implementado quando TaskApiService estiver pronto
     * Por enquanto retorna lista vazia
     *
     * @param projectId ID do projeto
     * @return Lista de tarefas
     */
    override suspend fun getTasksByProject(projectId: String): List<Task> {
        // TODO: Implementar quando TaskApiService estiver pronto
        println("⚠️ getTasksByProject ainda não implementado - aguardando TaskApiService")
        return emptyList()
    }

    /**
     * Cria novo projeto
     *
     * Requer permissão: CRIAR_PROJETO (ADMIN, GESTOR_OBRAS)
     *
     * @param project Dados do projeto a criar
     * @return Project criado com ID do backend
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    override suspend fun createProject(project: Project): Project {
        return try {
            val createDto = project.toCreateDto()
            val createdDto = projectApiService.createProject(createDto)
            createdDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao criar projeto: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza projeto existente
     *
     * Requer permissão: EDITAR_PROJETO_PROPRIO (GESTOR_OBRAS) ou EDITAR_QUALQUER_PROJETO (ADMIN)
     *
     * @param project Dados do projeto a atualizar
     * @return Project atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    override suspend fun updateProject(project: Project): Project {
        return try {
            val updateDto = project.toUpdateDto()
            val updatedDto = projectApiService.updateProject(project.id, updateDto)
            updatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao atualizar projeto ${project.id}: ${e.message}")
            throw e
        }
    }

    /**
     * Remove projeto (hard delete)
     *
     * Requer permissão: APAGAR_PROJETO_PROPRIO (GESTOR_OBRAS) ou APAGAR_QUALQUER_PROJETO (ADMIN)
     *
     * ⚠️ RECOMENDAÇÃO: Usar cancelProject() ao invés de deleteProject() para soft delete
     *
     * @param projectId ID do projeto a remover
     * @return true se removido com sucesso, false se não encontrado
     * @throws Exception se sem permissão (403) ou erro
     */
    override suspend fun deleteProject(projectId: String): Boolean {
        return try {
            projectApiService.deleteProject(projectId)
            true
        } catch (e: Exception) {
            if (e.message?.contains("não encontrado") == true) {
                false
            } else {
                println("❌ Erro ao remover projeto $projectId: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Cancela projeto (soft delete recomendado)
     *
     * Marca o projeto como CANCELLED ao invés de deletar
     *
     * @param projectId ID do projeto a cancelar
     * @return Project com status CANCELLED
     * @throws Exception se sem permissão ou erro
     */
    suspend fun cancelProject(projectId: String): Project {
        return try {
            val cancelledDto = projectApiService.cancelProject(projectId)
            cancelledDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao cancelar projeto $projectId: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza apenas o progresso do projeto
     *
     * Helper method para atualizar progresso sem precisar enviar todos os campos
     *
     * @param projectId ID do projeto
     * @param progress Progresso (0.0 a 100.0)
     * @return Project atualizado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun updateProgress(projectId: String, progress: Double): Project {
        return try {
            val updatedDto = projectApiService.updateProgress(projectId, progress)
            updatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao atualizar progresso do projeto $projectId: ${e.message}")
            throw e
        }
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    /**
     * Converte ProjectDto (backend) para Project (domain model)
     */
    private fun ProjectDto.toDomainModel(): Project {
        return Project(
            id = this.id,
            name = this.name,
            description = this.description,
            client = this.client,
            location = this.location,
            budget = this.budget,
            startDate = LocalDate.parse(this.startDate),
            endDate = LocalDate.parse(this.endDate)
        )
    }

    /**
     * Converte Project (domain) para CreateProjectDto (backend)
     */
    private fun Project.toCreateDto(): CreateProjectDto {
        return CreateProjectDto(
            name = this.name,
            description = this.description,
            client = this.client,
            location = this.location,
            budget = this.budget,
            startDate = this.startDate.toString(),
            endDate = this.endDate.toString(),
            status = "PLANNING",  // Status inicial padrão
            employeeIds = emptyList(),
            teamIds = emptyList()
        )
    }

    /**
     * Converte Project (domain) para UpdateProjectDto (backend)
     */
    private fun Project.toUpdateDto(): UpdateProjectDto {
        return UpdateProjectDto(
            name = this.name,
            description = this.description,
            client = this.client,
            location = this.location,
            budget = this.budget,
            startDate = this.startDate.toString(),
            endDate = this.endDate.toString()
        )
    }
}
