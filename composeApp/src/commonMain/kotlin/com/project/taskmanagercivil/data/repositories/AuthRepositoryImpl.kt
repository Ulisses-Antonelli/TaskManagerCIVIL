package com.project.taskmanagercivil.data.repositories

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.api.AuthApiService
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.domain.repositories.AuthRepository

/**
 * Implementação do AuthRepository usando backend real via AuthApiService
 *
 * Integração com Spring Boot backend:
 * - POST /api/auth/login - Login com username/password
 * - POST /api/auth/refresh - Renovar token JWT
 * - POST /api/auth/logout - Fazer logout
 *
 * Armazena token JWT no ApiClient para requisições autenticadas
 */
class AuthRepositoryImpl(
    private val authApiService: AuthApiService = AuthApiService()
) : AuthRepository {
    private var currentUser: User? = null
    private var refreshToken: String? = null

    /**
     * Realiza login usando backend Spring Boot
     * Converte username/email para username (backend usa username, não email)
     */
    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            // Backend usa username, não email
            // Se usuário digitar email, extrair parte antes do @
            val username = if (email.contains("@")) {
                email.substringBefore("@")
            } else {
                email
            }

            // Chama API de login
            val loginResponse = authApiService.login(username, password)

            // Armazena refresh token para renovação futura
            refreshToken = loginResponse.refreshToken

            // Converte UserDto para User domain model
            val user = User(
                id = loginResponse.user.id,
                name = loginResponse.user.fullName,
                email = loginResponse.user.email,
                username = loginResponse.user.username,
                roles = loginResponse.user.roles.map { roleString ->
                    when (roleString) {
                        "ROLE_ADMIN" -> Role.ADMIN
                        "ROLE_MANAGER" -> Role.GESTOR_OBRAS
                        "ROLE_USER" -> Role.LIDER_EQUIPE
                        else -> Role.LIDER_EQUIPE // Default
                    }
                },
                avatarUrl = null,
                isActive = true
            )

            currentUser = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Email ou senha incorretos"))
        }
    }

    /**
     * Faz logout removendo token e dados do usuário
     */
    override suspend fun logout() {
        try {
            authApiService.logout()
        } catch (e: Exception) {
            // Mesmo se falhar, limpa localmente
        } finally {
            currentUser = null
            refreshToken = null
        }
    }

    /**
     * Verifica se há usuário autenticado
     * TODO: Verificar se token JWT ainda é válido
     */
    override suspend fun isAuthenticated(): Boolean {
        return currentUser != null && ApiClient.getAuthToken() != null
    }

    /**
     * Obtém usuário atual
     */
    override suspend fun getCurrentUser(): User? {
        return currentUser
    }

    /**
     * Envia email de recuperação de senha
     * TODO: Implementar endpoint no backend
     */
    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            // TODO: Chamar endpoint /api/auth/forgot-password quando estiver pronto
            println("Email de recuperação enviado para: $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao enviar email de recuperação"))
        }
    }

    /**
     * Reseta senha com token
     * TODO: Implementar endpoint no backend
     */
    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            // TODO: Chamar endpoint /api/auth/reset-password quando estiver pronto
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Erro ao resetar senha"))
        }
    }
}
