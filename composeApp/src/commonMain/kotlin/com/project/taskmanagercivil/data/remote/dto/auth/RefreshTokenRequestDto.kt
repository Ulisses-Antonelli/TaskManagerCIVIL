package com.project.taskmanagercivil.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO para requisição de renovação de token
 * Contrato de API para Spring Boot backend
 *
 * Endpoint: POST /api/auth/refresh
 */
@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String
)
