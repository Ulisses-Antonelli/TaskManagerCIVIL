package com.project.taskmanagercivil.data.repositories

import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.domain.repositories.AuthRepository
import kotlinx.coroutines.delay

/**
 * Implementação mock do AuthRepository para desenvolvimento
 */
class AuthRepositoryImpl : AuthRepository {
    private var currentUser: User? = null

    // Usuários de teste
    private val mockUsers = listOf(
        User(
            id = "1",
            name = "Admin Sistema",
            email = "admin@taskmanager.com",
            roles = listOf(Role.ADMIN),
            avatarUrl = null,
            isActive = true
        ),
        User(
            id = "2",
            name = "João Silva",
            email = "joao.silva@taskmanager.com",
            roles = listOf(Role.GESTOR_OBRAS),
            avatarUrl = null,
            isActive = true
        ),
        User(
            id = "3",
            name = "Maria Santos",
            email = "maria.santos@taskmanager.com",
            roles = listOf(Role.LIDER_EQUIPE),
            avatarUrl = null,
            isActive = true
        )
    )

    // Mock de senhas (em produção, isso seria verificado no backend)
    private val mockPasswords = mapOf(
        "admin@taskmanager.com" to "admin123",
        "joao.silva@taskmanager.com" to "senha123",
        "maria.santos@taskmanager.com" to "senha123"
    )

    override suspend fun login(email: String, password: String): Result<User> {
        // Simula latência de rede
        delay(500)

        val user = mockUsers.find { it.email.equals(email, ignoreCase = true) }
        val storedPassword = mockPasswords[email.lowercase()]

        return if (user != null && storedPassword == password) {
            currentUser = user
            Result.success(user)
        } else {
            Result.failure(Exception("Email ou senha incorretos"))
        }
    }

    override suspend fun logout() {
        delay(200)
        currentUser = null
    }

    override suspend fun isAuthenticated(): Boolean {
        return currentUser != null
    }

    override suspend fun getCurrentUser(): User? {
        return currentUser
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        // Simula latência de rede
        delay(800)

        val user = mockUsers.find { it.email.equals(email, ignoreCase = true) }
        return if (user != null) {
            // Em produção, aqui seria enviado um email real
            println("Email de recuperação enviado para: $email")
            Result.success(Unit)
        } else {
            Result.failure(Exception("Email não encontrado"))
        }
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        // Simula latência de rede
        delay(500)

        // Em produção, verificaria o token e atualizaria a senha no backend
        return Result.success(Unit)
    }
}
