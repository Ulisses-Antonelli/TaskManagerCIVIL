package com.project.taskmanagercivil.domain.models

data class User(
    val id: String,
    val name: String,
    val role: String, //UserRole
    val email: String,
    val avatarUrl: String? = null
    //val isActive: Boolean = true
)

/*
enum class UserRole(val displayName: String) {
    ADMIN("Administrador"),
    PROJECT_MANAGER("Gerente de Projeto"),
    ENGINEER("Engenheiro"),
    ARCHITECT("Arquiteto"),
    TECHNICIAN("Técnico"),
    FOREMAN("Mestre de Obra"),
    WORKER("Operário")
}
 */