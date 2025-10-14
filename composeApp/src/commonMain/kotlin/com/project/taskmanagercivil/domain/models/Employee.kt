package com.project.taskmanagercivil.domain.models

import kotlinx.datetime.LocalDate

/**
 * Modelo de dados para Colaborador
 */
data class Employee(
    val id: String,
    val fullName: String,
    val role: String, // Cargo/Função (ex: "Engenheiro Civil", "Mestre de Obras")
    val email: String,
    val phone: String? = null,
    val cpf: String? = null,
    val hireDate: LocalDate, // Data de admissão
    val terminationDate: LocalDate? = null, // Data de demissão (null se ativo)
    val avatarUrl: String? = null,
    val projectIds: List<String> = emptyList(), // IDs dos projetos que o colaborador é responsável
    val isActive: Boolean = true // Ativo ou inativo (demitido)
) {
    /**
     * Verifica se o colaborador está ativo (não demitido)
     */
    fun isCurrentlyActive(): Boolean = terminationDate == null && isActive
}
