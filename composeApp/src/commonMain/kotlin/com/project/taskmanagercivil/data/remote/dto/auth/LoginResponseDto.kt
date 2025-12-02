package com.project.taskmanagercivil.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO para resposta de login
 * Contrato de API para Spring Boot backend com Spring Security
 *
 * Endpoint: POST /api/auth/login
 * Response: 200 OK
 */
@Serializable
data class LoginResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Int, // Segundos até expiração
    val user: UserDto
)

/**
 * DTO com informações do usuário autenticado
 */
@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val email: String,
    val fullName: String,
    val roles: List<String>, // e.g., ["ROLE_ADMIN", "ROLE_USER"]
    val createdAt: String,
    val updatedAt: String
)
