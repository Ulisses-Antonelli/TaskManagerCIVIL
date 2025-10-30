package com.project.taskmanagercivil.presentation.components

import androidx.compose.runtime.Composable
import com.project.taskmanagercivil.domain.models.Permission
import com.project.taskmanagercivil.domain.models.PermissionChecker
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User

/**
 * Componente que controla a visibilidade de elementos da UI baseado em permissões
 *
 * Uso:
 * ```
 * PermissionGate(currentUser, Permission.CRIAR_PROJETO) {
 *     Button(onClick = { ... }) { Text("Criar Projeto") }
 * }
 * ```
 */
@Composable
fun PermissionGate(
    user: User?,
    permission: Permission,
    content: @Composable () -> Unit
) {
    if (PermissionChecker.hasPermission(user, permission)) {
        content()
    }
}

/**
 * Versão que verifica múltiplas permissões (OR - basta ter UMA das permissões)
 */
@Composable
fun PermissionGateAny(
    user: User?,
    permissions: List<Permission>,
    content: @Composable () -> Unit
) {
    if (permissions.any { PermissionChecker.hasPermission(user, it) }) {
        content()
    }
}

/**
 * Versão que verifica múltiplas permissões (AND - precisa ter TODAS as permissões)
 */
@Composable
fun PermissionGateAll(
    user: User?,
    permissions: List<Permission>,
    content: @Composable () -> Unit
) {
    if (permissions.all { PermissionChecker.hasPermission(user, it) }) {
        content()
    }
}

/**
 * Versão que verifica papéis ao invés de permissões
 */
@Composable
fun RoleGate(
    user: User?,
    roles: List<Role>,
    content: @Composable () -> Unit
) {
    if (PermissionChecker.hasAnyRole(user, *roles.toTypedArray())) {
        content()
    }
}

/**
 * Versão especial para ADMIN
 */
@Composable
fun AdminOnly(
    user: User?,
    content: @Composable () -> Unit
) {
    if (PermissionChecker.isAdmin(user)) {
        content()
    }
}
