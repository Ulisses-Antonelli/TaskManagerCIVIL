package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.UserApiService
import com.project.taskmanagercivil.data.remote.dto.user.*
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Instant

/**
 * Implementação do repositório de usuários com integração ao backend
 *
 * Esta implementação se conecta ao backend via UserApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ PERMISSÕES:
 * - ADMIN: Acesso total a todos os métodos
 * - Outros usuários: Apenas visualizam/editam próprio perfil
 *
 * @param userApiService Serviço de API para usuários
 */
class UserRepositoryImpl(
    private val userApiService: UserApiService = UserApiService()
) : UserRepository {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val users: Flow<List<User>> = _users.asStateFlow()

    /**
     * Carrega usuários do backend e atualiza o estado local
     *
     * Permissão necessária: ADMIN
     *
     * @param isActive Filtrar por status ativo/inativo (opcional)
     * @param role Filtrar por role (opcional)
     */
    suspend fun loadUsers(
        isActive: Boolean? = null,
        role: String? = null
    ) {
        try {
            val usersDto = userApiService.getAllUsers(isActive, role)
            _users.value = usersDto.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao carregar usuários: ${e.message}")
            throw e
        }
    }

    override fun getAllUsers(): Flow<List<User>> = users

    override fun getUserById(id: String): User? {
        return _users.value.find { it.id == id }
    }

    override fun getUsersByRole(role: String): List<User> {
        return _users.value.filter { user ->
            user.roles.any { it.name == role }
        }
    }

    override fun getActiveUsers(): List<User> {
        return _users.value.filter { it.isActive }
    }

    override fun getInactiveUsers(): List<User> {
        return _users.value.filter { !it.isActive }
    }

    /**
     * Cria novo usuário (versão async que integra com backend)
     *
     * Permissão necessária: ADMIN
     *
     * @param username Nome de usuário (único)
     * @param email Email do usuário
     * @param password Senha (será hasheada no backend)
     * @param fullName Nome completo
     * @param roles Lista de roles
     * @param isActive Se usuário começa ativo
     * @return User criado com ID do backend
     * @throws Exception se sem permissão ou erro
     */
    suspend fun createUser(
        username: String,
        email: String,
        password: String,
        fullName: String,
        roles: List<String> = listOf("ROLE_FUNCIONARIO"),
        isActive: Boolean = true
    ): User {
        return try {
            val createDto = CreateUserDto(
                username = username,
                email = email,
                password = password,
                fullName = fullName,
                roles = roles,
                isActive = isActive
            )
            val createdDto = userApiService.createUser(createDto)
            val createdUser = createdDto.toDomainModel()

            // Atualiza cache local
            val currentList = _users.value.toMutableList()
            currentList.add(createdUser)
            _users.value = currentList

            createdUser
        } catch (e: Exception) {
            println("❌ Erro ao criar usuário: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza usuário existente (versão async que integra com backend)
     *
     * Permissão: ADMIN ou próprio usuário (campos limitados)
     *
     * @param id ID do usuário
     * @param email Novo email (opcional)
     * @param fullName Novo nome completo (opcional)
     * @param isActive Novo status ativo (opcional, ADMIN apenas)
     * @return User atualizado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun updateUser(
        id: String,
        email: String? = null,
        fullName: String? = null,
        isActive: Boolean? = null
    ): User {
        return try {
            val updateDto = UpdateUserDto(
                email = email,
                fullName = fullName,
                isActive = isActive
            )
            val updatedDto = userApiService.updateUser(id, updateDto)
            val updatedUser = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _users.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = updatedUser
                _users.value = currentList
            }

            updatedUser
        } catch (e: Exception) {
            println("❌ Erro ao atualizar usuário $id: ${e.message}")
            throw e
        }
    }

    /**
     * Remove usuário (soft delete)
     *
     * Permissão necessária: ADMIN
     *
     * @param id ID do usuário
     * @throws Exception se sem permissão ou erro
     */
    suspend fun deleteUser(id: String) {
        try {
            userApiService.deleteUser(id)

            // Remove do cache local
            val currentList = _users.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList.removeAt(index)
                _users.value = currentList
            }
        } catch (e: Exception) {
            println("❌ Erro ao remover usuário $id: ${e.message}")
            throw e
        }
    }

    /**
     * Ativa usuário desativado
     *
     * Permissão necessária: ADMIN
     *
     * @param id ID do usuário
     * @return User atualizado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun activateUser(id: String): User {
        return try {
            val updatedDto = userApiService.activateUser(id)
            val updatedUser = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _users.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = updatedUser
                _users.value = currentList
            }

            updatedUser
        } catch (e: Exception) {
            println("❌ Erro ao ativar usuário $id: ${e.message}")
            throw e
        }
    }

    /**
     * Desativa usuário ativo
     *
     * Permissão necessária: ADMIN
     *
     * @param id ID do usuário
     * @return User atualizado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun deactivateUser(id: String): User {
        return try {
            val updatedDto = userApiService.deactivateUser(id)
            val updatedUser = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _users.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = updatedUser
                _users.value = currentList
            }

            updatedUser
        } catch (e: Exception) {
            println("❌ Erro ao desativar usuário $id: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza roles do usuário
     *
     * Permissão necessária: ADMIN
     *
     * @param id ID do usuário
     * @param roles Lista de roles (ex: ["ROLE_ADMIN", "ROLE_GESTOR_OBRAS"])
     * @return User atualizado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun updateUserRoles(id: String, roles: List<String>): User {
        return try {
            val rolesDto = UpdateRolesDto(roles = roles)
            val updatedDto = userApiService.updateUserRoles(id, rolesDto)
            val updatedUser = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _users.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList[index] = updatedUser
                _users.value = currentList
            }

            updatedUser
        } catch (e: Exception) {
            println("❌ Erro ao atualizar roles do usuário $id: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza senha do usuário
     *
     * Permissão: Próprio usuário ou ADMIN
     *
     * @param id ID do usuário
     * @param currentPassword Senha atual
     * @param newPassword Nova senha
     * @throws Exception se senha atual incorreta ou sem permissão
     */
    suspend fun updatePassword(
        id: String,
        currentPassword: String,
        newPassword: String
    ) {
        try {
            val passwordDto = UpdatePasswordDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )
            userApiService.updatePassword(id, passwordDto)
        } catch (e: Exception) {
            println("❌ Erro ao atualizar senha do usuário $id: ${e.message}")
            throw e
        }
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    private fun UserDto.toDomainModel(): User {
        return User(
            id = this.id,
            name = this.fullName,
            email = this.email,
            username = this.username,
            roles = this.roles.mapNotNull { roleString ->
                try {
                    // Remove "ROLE_" prefix se existir
                    val roleName = roleString.removePrefix("ROLE_")
                    Role.valueOf(roleName)
                } catch (e: IllegalArgumentException) {
                    println("⚠️ Role desconhecido: $roleString")
                    null
                }
            },
            isActive = this.isActive,
            createdAt = Instant.parse(this.createdAt),
            lastLogin = this.lastLoginAt?.let { Instant.parse(it) }
        )
    }
}
