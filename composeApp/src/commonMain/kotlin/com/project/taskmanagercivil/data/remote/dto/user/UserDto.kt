package com.project.taskmanagercivil.data.remote.dto.user

import kotlinx.serialization.Serializable

/**
 * DTOs para Usuários - compatível com Spring Boot backend
 *
 * Endpoints do backend:
 * - GET /api/users - Lista usuários (ADMIN apenas)
 * - GET /api/users/{id} - Busca usuário por ID
 * - POST /api/users - Cria novo usuário (ADMIN apenas)
 * - PUT /api/users/{id} - Atualiza usuário (ADMIN apenas)
 * - DELETE /api/users/{id} - Remove usuário (ADMIN apenas)
 * - PATCH /api/users/{id}/activate - Ativa usuário (ADMIN apenas)
 * - PATCH /api/users/{id}/deactivate - Desativa usuário (ADMIN apenas)
 * - PATCH /api/users/{id}/roles - Atualiza roles (ADMIN apenas)
 *
 * ⚠️ PERMISSÕES:
 * Apenas ADMIN pode gerenciar usuários
 * Usuários normais só podem ver/editar seu próprio perfil
 *
 * Roles disponíveis:
 * - ROLE_ADMIN: Acesso total ao sistema
 * - ROLE_GESTOR_OBRAS: Gerencia projetos e equipes
 * - ROLE_LIDER_EQUIPE: Gerencia sua equipe
 * - ROLE_FUNCIONARIO: Acesso básico
 */

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val roles: List<String>,  // Lista de roles (ex: ["ROLE_ADMIN", "ROLE_USER"])
    val isActive: Boolean,
    val createdAt: String,  // ISO 8601: "2024-01-01T00:00:00Z"
    val updatedAt: String,
    val lastLoginAt: String? = null
)

/**
 * DTO para criar usuário (sem campos auto-gerados)
 */
@Serializable
data class CreateUserDto(
    val username: String,
    val email: String,
    val password: String,  // Senha será hasheada no backend
    val fullName: String,
    val roles: List<String> = listOf("ROLE_FUNCIONARIO"),  // Padrão: funcionário
    val isActive: Boolean = true
)

/**
 * DTO para atualizar usuário
 */
@Serializable
data class UpdateUserDto(
    val email: String? = null,
    val fullName: String? = null,
    val isActive: Boolean? = null
)

/**
 * DTO para atualizar senha
 */
@Serializable
data class UpdatePasswordDto(
    val currentPassword: String,
    val newPassword: String
)

/**
 * DTO para atualizar roles do usuário
 */
@Serializable
data class UpdateRolesDto(
    val roles: List<String>
)
