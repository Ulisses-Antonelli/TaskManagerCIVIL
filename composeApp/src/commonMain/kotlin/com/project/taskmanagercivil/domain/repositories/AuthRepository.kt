package com.project.taskmanagercivil.domain.repositories

import com.project.taskmanagercivil.domain.models.User

/**
 * Repository para gerenciar autenticação de usuários
 */
interface AuthRepository {
    /**
     * Realiza login com email e senha
     * @return User se autenticado, null caso contrário
     */
    suspend fun login(email: String, password: String): Result<User>

    /**
     * Realiza logout do usuário atual
     */
    suspend fun logout()

    /**
     * Verifica se há um usuário autenticado
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Obtém o usuário atualmente autenticado
     */
    suspend fun getCurrentUser(): User?

    /**
     * Envia email de recuperação de senha
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Reseta a senha com o token recebido por email
     */
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
}
