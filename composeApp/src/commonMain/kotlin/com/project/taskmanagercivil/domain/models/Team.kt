package com.project.taskmanagercivil.domain.models

import kotlinx.datetime.LocalDate

/**
 * Modelo de dados para Time/Setor
 */
data class Team(
    val id: String,
    val name: String, // Nome do time (ex: "Arquitetura", "Hidráulica - Obra")
    val department: TeamDepartment, // Departamento/Setor
    val description: String,
    val leaderId: String?, // ID do líder/responsável do time
    val memberIds: List<String> = emptyList(), // IDs dos colaboradores membros
    val projectIds: List<String> = emptyList(), // IDs dos projetos que o time está trabalhando
    val createdDate: LocalDate,
    val isActive: Boolean = true
) {
    /**
     * Retorna o número total de membros do time
     */
    fun getTotalMembers(): Int = memberIds.size

    /**
     * Retorna o número de projetos ativos
     */
    fun getTotalProjects(): Int = projectIds.size
}

/**
 * Enumeração dos departamentos/setores
 */
enum class TeamDepartment(val displayName: String) {
    ARCHITECTURE("Arquitetura"),
    STRUCTURE("Estruturas/Fundações"),
    HYDRAULIC("Hidráulica - Obra"),
    ELECTRICAL("Elétrica - Obra"),
    MASONRY("Alvenaria"),
    FINISHING("Acabamento"),
    CLEANING("Limpeza/Canteiro de Obras"),
    SAFETY("Segurança do Trabalho"),
    ADMINISTRATION("Administração"),
    PURCHASING("Compras"),
    QUALITY("Qualidade"),
    PLANNING("Planejamento")
}
