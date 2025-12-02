package com.project.taskmanagercivil.data.remote.dto.team

import kotlinx.serialization.Serializable

/**
 * DTO para Team (Time/Setor) - compatível com Spring Boot backend
 *
 * Endpoints do backend:
 * - GET /api/teams - Lista todos os times
 * - GET /api/teams/{id} - Busca time por ID
 * - POST /api/teams - Cria novo time
 * - PUT /api/teams/{id} - Atualiza time
 * - DELETE /api/teams/{id} - Remove time
 *
 * Permissões por Role:
 * - ADMIN: Pode criar, editar qualquer, apagar qualquer
 * - GESTOR_OBRAS: Pode criar, editar próprio, ver todos
 * - LIDER_EQUIPE: Pode editar próprio time, ver todos
 * - FUNCIONARIO: Pode ver todos (read-only)
 */
@Serializable
data class TeamDto(
    val id: String,
    val name: String,
    val department: String,  // TeamDepartment enum value
    val description: String,
    val leaderId: String? = null,
    val memberIds: List<String> = emptyList(),
    val projectIds: List<String> = emptyList(),
    val createdDate: String,  // ISO 8601: "2024-01-01"
    val isActive: Boolean = true,
    val createdAt: String,  // ISO 8601: "2024-01-01T00:00:00Z"
    val updatedAt: String,  // ISO 8601: "2024-01-15T10:30:00Z"
    val createdBy: String? = null,
    val updatedBy: String? = null
)

/**
 * DTO para criação de Team (sem campos auto-gerados)
 */
@Serializable
data class CreateTeamDto(
    val name: String,
    val department: String,  // TeamDepartment enum value
    val description: String,
    val leaderId: String? = null,
    val memberIds: List<String> = emptyList(),
    val projectIds: List<String> = emptyList(),
    val isActive: Boolean = true
)

/**
 * DTO para atualização de Team (campos opcionais)
 */
@Serializable
data class UpdateTeamDto(
    val name: String? = null,
    val department: String? = null,
    val description: String? = null,
    val leaderId: String? = null,
    val memberIds: List<String>? = null,
    val projectIds: List<String>? = null,
    val isActive: Boolean? = null
)
