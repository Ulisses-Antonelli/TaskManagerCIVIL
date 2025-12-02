package com.project.taskmanagercivil.data.remote.dto.employee

import kotlinx.serialization.Serializable

/**
 * DTO para Employee (Colaborador) - compatível com Spring Boot backend
 *
 * Estrutura esperada do backend:
 * - GET /api/employees - Lista todos os colaboradores
 * - GET /api/employees/{id} - Busca colaborador por ID
 * - POST /api/employees - Cria novo colaborador
 * - PUT /api/employees/{id} - Atualiza colaborador
 * - DELETE /api/employees/{id} - Remove colaborador
 *
 * Permissões por Role (definidas no Permission.kt):
 * - ADMIN: Pode criar, editar, remover qualquer colaborador
 * - GESTOR_OBRAS: Pode adicionar e editar colaboradores
 * - LIDER_EQUIPE: Pode adicionar colaboradores à sua equipe
 * - FUNCIONARIO: Apenas visualização
 */
@Serializable
data class EmployeeDto(
    val id: String,
    val userId: String? = null, // Referência ao User (se houver usuário de sistema vinculado)
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val cpf: String? = null,
    val role: String, // Cargo/Função (ex: "Engenheiro Civil", "Mestre de Obras")
    val department: String? = null, // ENGINEERING, ARCHITECTURE, CONSTRUCTION, etc
    val hireDate: String, // ISO 8601: "2023-01-15"
    val terminationDate: String? = null, // ISO 8601: "2024-06-30" (null se ativo)
    val projectIds: List<String> = emptyList(), // IDs dos projetos
    val teamIds: List<String> = emptyList(), // IDs das equipes
    val status: String = "ACTIVE", // ACTIVE, INACTIVE, TERMINATED
    val avatarUrl: String? = null,
    val createdAt: String, // ISO 8601: "2024-01-01T00:00:00Z"
    val updatedAt: String, // ISO 8601: "2024-01-15T10:30:00Z"
    val createdBy: String? = null, // ID do usuário que criou
    val updatedBy: String? = null // ID do usuário que atualizou
)

/**
 * DTO para criação de Employee (sem campos auto-gerados)
 */
@Serializable
data class CreateEmployeeDto(
    val userId: String? = null,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val cpf: String? = null,
    val role: String,
    val department: String? = null,
    val hireDate: String, // ISO 8601: "2023-01-15"
    val projectIds: List<String> = emptyList(),
    val teamIds: List<String> = emptyList(),
    val avatarUrl: String? = null
)

/**
 * DTO para atualização de Employee (campos opcionais)
 */
@Serializable
data class UpdateEmployeeDto(
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val role: String? = null,
    val department: String? = null,
    val terminationDate: String? = null,
    val projectIds: List<String>? = null,
    val teamIds: List<String>? = null,
    val status: String? = null,
    val avatarUrl: String? = null
)
