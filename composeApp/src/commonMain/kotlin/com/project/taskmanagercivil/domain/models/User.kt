package com.project.taskmanagercivil.domain.models

/**
 * Usuário do sistema
 * @param roles Lista de papéis - um usuário pode ter múltiplos papéis
 *              Ex: pode ser LIDER_EQUIPE de uma equipe e FUNCIONARIO em outra
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val roles: List<Role> = listOf(Role.FUNCIONARIO), // Papéis do usuário
    val avatarUrl: String? = null,
    val isActive: Boolean = true
) {
    /**
     * Verifica se o usuário tem um papel específico
     */
    fun hasRole(role: Role): Boolean = roles.contains(role)

    /**
     * Verifica se o usuário tem permissão para uma ação
     */
    fun hasPermission(permission: Permission): Boolean {
        return Permission.hasPermission(roles, permission)
    }

    /**
     * Verifica se o usuário é admin
     */
    val isAdmin: Boolean get() = hasRole(Role.ADMIN)

    /**
     * Retorna o papel principal (maior hierarquia)
     */
    val primaryRole: Role
        get() = when {
            hasRole(Role.ADMIN) -> Role.ADMIN
            hasRole(Role.GESTOR_OBRAS) -> Role.GESTOR_OBRAS
            hasRole(Role.LIDER_EQUIPE) -> Role.LIDER_EQUIPE
            else -> Role.FUNCIONARIO
        }

    /**
     * Nome do papel principal para exibição
     */
    val primaryRoleDisplayName: String get() = primaryRole.displayName
}