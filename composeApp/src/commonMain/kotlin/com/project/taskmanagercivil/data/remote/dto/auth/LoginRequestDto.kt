package com.project.taskmanagercivil.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO para requisição de login
 * Contrato de API para Spring Boot backend
 *
 * Endpoint: POST /api/auth/login
 */
@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)
