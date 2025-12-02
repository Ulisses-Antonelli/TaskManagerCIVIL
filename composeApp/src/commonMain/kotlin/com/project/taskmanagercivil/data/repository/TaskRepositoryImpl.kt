package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.TaskApiService
import com.project.taskmanagercivil.data.remote.dto.task.*
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.domain.repository.TaskRepository
import kotlinx.datetime.LocalDate

/**
 * Implementação do repositório de tarefas com integração ao backend
 *
 * Esta implementação se conecta ao backend via TaskApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ IMPORTANTE: Task tem dependências de User e Project
 * Esta implementação usa IDs temporários para User e Project
 * até que os repositórios estejam disponíveis via DI
 *
 * @param taskApiService Serviço de API para tarefas
 */
class TaskRepositoryImpl(
    private val taskApiService: TaskApiService = TaskApiService()
) : TaskRepository {

    /**
     * Lista todas as tarefas do backend
     *
     * @return Lista de tarefas convertidas para domain model
     * @throws Exception se houver erro de rede ou autenticação
     */
    override suspend fun getAllTasks(): List<Task> {
        return try {
            val taskDtos = taskApiService.getAllTasks()
            taskDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao buscar tarefas: ${e.message}")
            throw e
        }
    }

    /**
     * Busca tarefa por ID
     *
     * @param id ID da tarefa
     * @return Task ou null se não encontrado
     * @throws Exception se houver erro de rede ou autenticação
     */
    override suspend fun getTaskById(id: String): Task? {
        return try {
            val taskDto = taskApiService.getTaskById(id)
            taskDto.toDomainModel()
        } catch (e: Exception) {
            if (e.message?.contains("não encontrada") == true) {
                null
            } else {
                println("❌ Erro ao buscar tarefa $id: ${e.message}")
                throw e
            }
        }
    }

    /**
     * Busca tarefas de um projeto
     *
     * @param projectId ID do projeto
     * @return Lista de tarefas do projeto
     * @throws Exception se houver erro
     */
    suspend fun getTasksByProject(projectId: String): List<Task> {
        return try {
            val taskDtos = taskApiService.getTasksByProject(projectId)
            taskDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao buscar tarefas do projeto $projectId: ${e.message}")
            throw e
        }
    }

    /**
     * Busca tarefas atribuídas a um usuário
     *
     * @param userId ID do usuário
     * @return Lista de tarefas atribuídas
     * @throws Exception se houver erro
     */
    suspend fun getTasksByAssignedUser(userId: String): List<Task> {
        return try {
            val taskDtos = taskApiService.getTasksByAssignedUser(userId)
            taskDtos.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao buscar tarefas do usuário $userId: ${e.message}")
            throw e
        }
    }

    /**
     * Cria nova tarefa
     *
     * Requer permissão: CRIAR_TAREFA (ADMIN, GESTOR_OBRAS, LIDER_EQUIPE)
     *
     * @param task Dados da tarefa a criar
     * @return Task criado com ID do backend
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    override suspend fun createTask(task: Task): Task {
        return try {
            val createDto = task.toCreateDto()
            val createdDto = taskApiService.createTask(createDto)
            createdDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao criar tarefa: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza tarefa existente
     *
     * Requer permissão:
     * - EDITAR_TAREFA_ATRIBUIDA (tarefas atribuídas ao usuário)
     * - EDITAR_TAREFA_PROPRIA (tarefas criadas pelo usuário)
     * - EDITAR_QUALQUER_TAREFA (ADMIN, GESTOR_OBRAS)
     *
     * @param task Dados da tarefa a atualizar
     * @return Task atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    override suspend fun updateTask(task: Task): Task {
        return try {
            val updateDto = task.toUpdateDto()
            val updatedDto = taskApiService.updateTask(task.id, updateDto)
            updatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao atualizar tarefa ${task.id}: ${e.message}")
            throw e
        }
    }

    /**
     * Remove tarefa (hard delete)
     *
     * Requer permissão:
     * - APAGAR_TAREFA_PROPRIA (tarefas criadas pelo usuário)
     * - APAGAR_QUALQUER_TAREFA (ADMIN, GESTOR_OBRAS)
     *
     * ⚠️ RECOMENDAÇÃO: Usar inactivateTask() ao invés de deleteTask() para soft delete
     *
     * @param taskId ID da tarefa a remover
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    override suspend fun deleteTask(taskId: String) {
        try {
            taskApiService.deleteTask(taskId)
        } catch (e: Exception) {
            println("❌ Erro ao remover tarefa $taskId: ${e.message}")
            throw e
        }
    }

    /**
     * Marca tarefa como inativa (soft delete recomendado)
     *
     * Marca a tarefa como INATIVA ao invés de deletar
     *
     * @param taskId ID da tarefa a inativar
     * @return Task com status INATIVA
     * @throws Exception se sem permissão ou erro
     */
    suspend fun inactivateTask(taskId: String): Task {
        return try {
            val inactivatedDto = taskApiService.inactivateTask(taskId)
            inactivatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao inativar tarefa $taskId: ${e.message}")
            throw e
        }
    }

    /**
     * Aprova tarefa
     *
     * Requer permissão: APROVAR_TAREFA_EQUIPE (LIDER_EQUIPE) ou APROVAR_QUALQUER_TAREFA (ADMIN, GESTOR_OBRAS)
     *
     * @param taskId ID da tarefa
     * @param aprovadoPor Nome do aprovador
     * @return Task aprovado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun approveTask(taskId: String, aprovadoPor: String): Task {
        return try {
            val approvedDto = taskApiService.approveTask(taskId, aprovadoPor)
            approvedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao aprovar tarefa $taskId: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza apenas o progresso da tarefa
     *
     * Helper method para atualizar progresso sem precisar enviar todos os campos
     *
     * @param taskId ID da tarefa
     * @param progress Progresso (0.0 a 1.0)
     * @return Task atualizado
     * @throws Exception se houver erro
     */
    suspend fun updateProgress(taskId: String, progress: Float): Task {
        return try {
            val updatedDto = taskApiService.updateProgress(taskId, progress)
            updatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao atualizar progresso da tarefa $taskId: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza apenas o status da tarefa
     *
     * Helper method para atualizar status sem precisar enviar todos os campos
     *
     * @param taskId ID da tarefa
     * @param status Novo status
     * @return Task atualizado
     * @throws Exception se houver erro
     */
    suspend fun updateStatus(taskId: String, status: TaskStatus): Task {
        return try {
            val updatedDto = taskApiService.updateStatus(taskId, status.name)
            updatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao atualizar status da tarefa $taskId: ${e.message}")
            throw e
        }
    }

    /**
     * Reatribui tarefa para outro usuário
     *
     * Helper method para reatribuir tarefa
     *
     * @param taskId ID da tarefa
     * @param newUserId ID do novo responsável
     * @return Task atualizado
     * @throws Exception se houver erro
     */
    suspend fun reassignTask(taskId: String, newUserId: String): Task {
        return try {
            val updatedDto = taskApiService.reassignTask(taskId, newUserId)
            updatedDto.toDomainModel()
        } catch (e: Exception) {
            println("❌ Erro ao reatribuir tarefa $taskId: ${e.message}")
            throw e
        }
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    /**
     * Converte TaskDto (backend) para Task (domain model)
     *
     * ⚠️ NOTA IMPORTANTE: Como Task precisa de objetos User e Project completos,
     * mas o DTO só tem IDs, estamos criando objetos temporários.
     * Em uma implementação completa, deveria buscar do UserRepository e ProjectRepository.
     */
    private fun TaskDto.toDomainModel(): Task {
        // TODO: Buscar User real do UserRepository quando disponível
        val tempUser = User(
            id = this.assignedToId,
            name = "User ${this.assignedToId}",
            email = "user@example.com",
            username = "user_${this.assignedToId}",
            roles = listOf(Role.FUNCIONARIO),
            avatarUrl = null,
            isActive = true
        )

        // TODO: Buscar Project real do ProjectRepository quando disponível
        val tempProject = Project(
            id = this.projectId,
            name = "Projeto ${this.projectId}",
            description = "",
            client = "",
            location = "",
            budget = 0.0,
            startDate = LocalDate.parse(this.startDate),
            endDate = LocalDate.parse(this.dueDate)
        )

        return Task(
            id = this.id,
            title = this.title,
            description = this.description,
            status = TaskStatus.valueOf(this.status),
            priority = TaskPriority.valueOf(this.priority),
            assignedTo = tempUser,
            project = tempProject,
            startDate = LocalDate.parse(this.startDate),
            dueDate = LocalDate.parse(this.dueDate),
            progress = this.progress,
            tags = this.tags,
            dependencies = this.dependencies,
            revisions = this.revisions.map { it.toDomainModel() },
            partialDeliveries = this.partialDeliveries.map { it.toDomainModel() },
            checklistItems = this.checklistItems.map { it.toDomainModel() },
            aprovado = this.aprovado,
            aprovadoPor = this.aprovadoPor,
            dataAprovacao = this.dataAprovacao?.let { LocalDate.parse(it) }
        )
    }

    private fun ChecklistItemDto.toDomainModel(): ChecklistItem {
        return ChecklistItem(
            text = this.text,
            isCompleted = this.isCompleted
        )
    }

    private fun TaskRevisionDto.toDomainModel(): TaskRevision {
        return TaskRevision(
            revisionNumber = this.revisionNumber,
            author = this.author,
            description = this.description,
            startDate = LocalDate.parse(this.startDate),
            deliveryDate = LocalDate.parse(this.deliveryDate),
            isEdited = this.isEdited
        )
    }

    private fun PartialDeliveryDto.toDomainModel(): PartialDelivery {
        return PartialDelivery(
            deliveryNumber = this.deliveryNumber,
            author = this.author,
            description = this.description,
            deliveryDate = LocalDate.parse(this.deliveryDate),
            completedItems = this.completedItems,
            totalItems = this.totalItems,
            isEdited = this.isEdited,
            aprovado = this.aprovado,
            aprovadoPor = this.aprovadoPor,
            dataAprovacao = this.dataAprovacao?.let { LocalDate.parse(it) }
        )
    }

    /**
     * Converte Task (domain) para CreateTaskDto (backend)
     */
    private fun Task.toCreateDto(): CreateTaskDto {
        return CreateTaskDto(
            title = this.title,
            description = this.description,
            status = this.status.name,
            priority = this.priority.name,
            assignedToId = this.assignedTo.id,
            projectId = this.project.id,
            startDate = this.startDate.toString(),
            dueDate = this.dueDate.toString(),
            tags = this.tags,
            dependencies = this.dependencies,
            checklistItems = this.checklistItems.map { it.toDto() }
        )
    }

    /**
     * Converte Task (domain) para UpdateTaskDto (backend)
     */
    private fun Task.toUpdateDto(): UpdateTaskDto {
        return UpdateTaskDto(
            title = this.title,
            description = this.description,
            status = this.status.name,
            priority = this.priority.name,
            assignedToId = this.assignedTo.id,
            startDate = this.startDate.toString(),
            dueDate = this.dueDate.toString(),
            progress = this.progress,
            tags = this.tags,
            dependencies = this.dependencies,
            revisions = this.revisions.map { it.toDto() },
            partialDeliveries = this.partialDeliveries.map { it.toDto() },
            checklistItems = this.checklistItems.map { it.toDto() },
            aprovado = this.aprovado,
            aprovadoPor = this.aprovadoPor,
            dataAprovacao = this.dataAprovacao?.toString()
        )
    }

    private fun ChecklistItem.toDto(): ChecklistItemDto {
        return ChecklistItemDto(
            text = this.text,
            isCompleted = this.isCompleted
        )
    }

    private fun TaskRevision.toDto(): TaskRevisionDto {
        return TaskRevisionDto(
            revisionNumber = this.revisionNumber,
            author = this.author,
            description = this.description,
            startDate = this.startDate.toString(),
            deliveryDate = this.deliveryDate.toString(),
            isEdited = this.isEdited
        )
    }

    private fun PartialDelivery.toDto(): PartialDeliveryDto {
        return PartialDeliveryDto(
            deliveryNumber = this.deliveryNumber,
            author = this.author,
            description = this.description,
            deliveryDate = this.deliveryDate.toString(),
            completedItems = this.completedItems,
            totalItems = this.totalItems,
            isEdited = this.isEdited,
            aprovado = this.aprovado,
            aprovadoPor = this.aprovadoPor,
            dataAprovacao = this.dataAprovacao?.toString()
        )
    }
}
