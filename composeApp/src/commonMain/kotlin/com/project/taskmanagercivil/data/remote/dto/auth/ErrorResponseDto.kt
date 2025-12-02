package com.project.taskmanagercivil.data.remote.dto.auth

import kotlinx.serialization.Serializable

/**
 * DTO para respostas de erro padronizadas
 * Segue o padrão Spring Boot Error Response
 *
 * Usado em todos os endpoints quando ocorre erro
 */
@Serializable
data class ErrorResponseDto(
    val timestamp: String,
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)
