package com.project.taskmanagercivil.data.remote.dto.project

import kotlinx.serialization.Serializable

/**
 * DTO para Project (Projeto/Obra) - compatível com Spring Boot backend
 *
 * Endpoints do backend:
 * - GET /api/projects - Lista todos os projetos
 * - GET /api/projects/{id} - Busca projeto por ID
 * - POST /api/projects - Cria novo projeto
 * - PUT /api/projects/{id} - Atualiza projeto
 * - DELETE /api/projects/{id} - Remove projeto
 *
 * Permissões por Role:
 * - ADMIN: Pode criar, editar qualquer, apagar qualquer
 * - GESTOR_OBRAS: Pode criar, editar próprio, apagar próprio, ver todos
 * - LIDER_EQUIPE: Pode ver todos (read-only)
 * - FUNCIONARIO: Pode ver todos (read-only)
 */
@Serializable
data class ProjectDto(
    val id: String,
    val name: String,
    val description: String,
    val client: String,
    val location: String,
    val budget: Double,
    val startDate: String,  // ISO 8601: "2024-01-01"
    val endDate: String,    // ISO 8601: "2025-12-31"
    val status: String,     // IN_PROGRESS, COMPLETED, PLANNING, ON_HOLD, CANCELLED
    val progress: Double = 0.0,  // 0.0 a 100.0
    val employeeIds: List<String> = emptyList(),
    val teamIds: List<String> = emptyList(),
    val createdAt: String,  // ISO 8601: "2024-01-01T00:00:00Z"
    val updatedAt: String,  // ISO 8601: "2024-01-15T10:30:00Z"
    val createdBy: String? = null,  // ID do usuário que criou
    val updatedBy: String? = null   // ID do usuário que atualizou
)

/**
 * DTO para criação de Project (sem campos auto-gerados)
 */
@Serializable
data class CreateProjectDto(
    val name: String,
    val description: String,
    val client: String,
    val location: String,
    val budget: Double,
    val startDate: String,  // ISO 8601: "2024-01-01"
    val endDate: String,    // ISO 8601: "2025-12-31"
    val status: String = "PLANNING",  // Status inicial
    val employeeIds: List<String> = emptyList(),
    val teamIds: List<String> = emptyList()
)

/**
 * DTO para atualização de Project (campos opcionais)
 */
@Serializable
data class UpdateProjectDto(
    val name: String? = null,
    val description: String? = null,
    val client: String? = null,
    val location: String? = null,
    val budget: Double? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val status: String? = null,
    val progress: Double? = null,
    val employeeIds: List<String>? = null,
    val teamIds: List<String>? = null
)
