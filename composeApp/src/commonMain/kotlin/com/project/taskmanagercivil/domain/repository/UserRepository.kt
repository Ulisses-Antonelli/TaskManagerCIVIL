package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.User
import kotlinx.coroutines.flow.Flow

/**
 * Interface de repositório para operações CRUD de usuários
 *
 * ⚠️ PERMISSÕES:
 * - ADMIN: Acesso total
 * - Outros: Apenas visualizam/editam próprio perfil
 */
interface UserRepository {

    /**
     * Retorna Flow de todos os usuários
     */
    fun getAllUsers(): Flow<List<User>>

    /**
     * Busca usuário por ID
     */
    fun getUserById(id: String): User?

    /**
     * Filtra usuários por role
     */
    fun getUsersByRole(role: String): List<User>

    /**
     * Filtra usuários ativos/inativos
     */
    fun getActiveUsers(): List<User>
    fun getInactiveUsers(): List<User>
}
