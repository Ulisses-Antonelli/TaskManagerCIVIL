package com.project.taskmanagercivil.data.remote.dto.task

import kotlinx.serialization.Serializable

/**
 * DTO para Task (Tarefa) - compatível com Spring Boot backend
 *
 * Endpoints do backend:
 * - GET /api/tasks - Lista todas as tarefas
 * - GET /api/tasks/{id} - Busca tarefa por ID
 * - GET /api/tasks/project/{projectId} - Lista tarefas de um projeto
 * - GET /api/tasks/assigned/{userId} - Lista tarefas atribuídas a um usuário
 * - POST /api/tasks - Cria nova tarefa
 * - PUT /api/tasks/{id} - Atualiza tarefa
 * - DELETE /api/tasks/{id} - Remove tarefa
 *
 * Permissões por Role:
 * - ADMIN: Pode criar, editar qualquer, apagar qualquer
 * - GESTOR_OBRAS: Pode criar, editar qualquer, aprovar, ver todas
 * - LIDER_EQUIPE: Pode criar, editar próprias tarefas, aprovar tarefas da equipe, ver todas
 * - FUNCIONARIO: Pode editar tarefas atribuídas (status, progresso), ver todas
 */
@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val status: String,  // TaskStatus enum value
    val priority: String,  // TaskPriority enum value
    val assignedToId: String,  // ID do User responsável
    val projectId: String,  // ID do Project
    val startDate: String,  // ISO 8601: "2024-01-01"
    val dueDate: String,  // ISO 8601: "2024-12-31"
    val progress: Float = 0f,  // 0.0 a 1.0 (0% a 100%)
    val tags: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),  // IDs de tarefas dependentes
    val revisions: List<TaskRevisionDto> = emptyList(),
    val partialDeliveries: List<PartialDeliveryDto> = emptyList(),
    val checklistItems: List<ChecklistItemDto> = emptyList(),
    val aprovado: Boolean = false,
    val aprovadoPor: String? = null,  // Nome do aprovador
    val dataAprovacao: String? = null,  // ISO 8601: "2024-01-15"
    val createdAt: String,  // ISO 8601: "2024-01-01T00:00:00Z"
    val updatedAt: String,  // ISO 8601: "2024-01-15T10:30:00Z"
    val createdBy: String? = null,
    val updatedBy: String? = null
)

/**
 * DTO para ChecklistItem (Item da lista de verificação)
 */
@Serializable
data class ChecklistItemDto(
    val text: String,
    val isCompleted: Boolean
)

/**
 * DTO para TaskRevision (Revisão de tarefa)
 */
@Serializable
data class TaskRevisionDto(
    val revisionNumber: Int,
    val author: String,
    val description: String,
    val startDate: String,  // ISO 8601: "2024-01-01"
    val deliveryDate: String,  // ISO 8601: "2024-01-15"
    val isEdited: Boolean = false
)

/**
 * DTO para PartialDelivery (Entrega parcial)
 */
@Serializable
data class PartialDeliveryDto(
    val deliveryNumber: Int,
    val author: String,
    val description: String,
    val deliveryDate: String,  // ISO 8601: "2024-01-15"
    val completedItems: Int,
    val totalItems: Int,
    val isEdited: Boolean = false,
    val aprovado: Boolean = false,
    val aprovadoPor: String? = null,
    val dataAprovacao: String? = null  // ISO 8601: "2024-01-16"
)

/**
 * DTO para criação de Task (sem campos auto-gerados)
 */
@Serializable
data class CreateTaskDto(
    val title: String,
    val description: String,
    val status: String = "TODO",  // Status inicial
    val priority: String = "MEDIUM",  // Prioridade padrão
    val assignedToId: String,
    val projectId: String,
    val startDate: String,  // ISO 8601: "2024-01-01"
    val dueDate: String,  // ISO 8601: "2024-12-31"
    val tags: List<String> = emptyList(),
    val dependencies: List<String> = emptyList(),
    val checklistItems: List<ChecklistItemDto> = emptyList()
)

/**
 * DTO para atualização de Task (campos opcionais)
 */
@Serializable
data class UpdateTaskDto(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val priority: String? = null,
    val assignedToId: String? = null,
    val startDate: String? = null,
    val dueDate: String? = null,
    val progress: Float? = null,
    val tags: List<String>? = null,
    val dependencies: List<String>? = null,
    val revisions: List<TaskRevisionDto>? = null,
    val partialDeliveries: List<PartialDeliveryDto>? = null,
    val checklistItems: List<ChecklistItemDto>? = null,
    val aprovado: Boolean? = null,
    val aprovadoPor: String? = null,
    val dataAprovacao: String? = null
)
