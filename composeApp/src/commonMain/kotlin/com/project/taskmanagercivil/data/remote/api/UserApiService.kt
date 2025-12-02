package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.user.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Serviço de API para gerenciamento de Usuários
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/users - Lista usuários
 * - GET /api/users/{id} - Busca usuário por ID
 * - POST /api/users - Cria novo usuário
 * - PUT /api/users/{id} - Atualiza usuário
 * - DELETE /api/users/{id} - Remove usuário
 * - PATCH /api/users/{id}/activate - Ativa usuário
 * - PATCH /api/users/{id}/deactivate - Desativa usuário
 * - PATCH /api/users/{id}/roles - Atualiza roles
 * - PUT /api/users/{id}/password - Atualiza senha
 *
 * ⚠️ PERMISSÕES:
 * - ADMIN: Acesso total a todos os endpoints
 * - Outros usuários: Apenas podem ver/editar seu próprio perfil
 *
 * Roles disponíveis:
 * - ROLE_ADMIN: Administrador do sistema
 * - ROLE_GESTOR_OBRAS: Gerente de obras/projetos
 * - ROLE_LIDER_EQUIPE: Líder de equipe
 * - ROLE_FUNCIONARIO: Funcionário (acesso básico)
 */
class UserApiService {

    /**
     * Lista todos os usuários
     *
     * Endpoint: GET /api/users
     * Permissão necessária: ADMIN apenas
     *
     * @param isActive Filtrar por status ativo/inativo (opcional)
     * @param role Filtrar por role (opcional)
     * @return Lista de UserDto
     * @throws Exception se sem permissão (403) ou erro
     */
    suspend fun getAllUsers(
        isActive: Boolean? = null,
        role: String? = null
    ): List<UserDto> {
        val response = ApiClient.httpClient.get("/users") {
            url {
                isActive?.let { parameters.append("isActive", it.toString()) }
                role?.let { parameters.append("role", it) }
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para listar usuários")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar usuários: ${response.status}")
        }
    }

    /**
     * Busca usuário por ID
     *
     * Endpoint: GET /api/users/{id}
     * Permissão: ADMIN ou próprio usuário
     *
     * @param id ID do usuário
     * @return UserDto
     * @throws Exception se não encontrado (404), sem permissão (403) ou erro
     */
    suspend fun getUserById(id: String): UserDto {
        val response = ApiClient.httpClient.get("/users/$id")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Usuário não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para visualizar este usuário")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar usuário: ${response.status}")
        }
    }

    /**
     * Cria novo usuário
     *
     * Endpoint: POST /api/users
     * Permissão necessária: ADMIN apenas
     *
     * ⚠️ IMPORTANTE: A senha será hasheada no backend (bcrypt)
     *
     * @param createDto Dados do novo usuário
     * @return UserDto criado
     * @throws Exception se sem permissão (403), dados inválidos (400) ou erro
     */
    suspend fun createUser(createDto: CreateUserDto): UserDto {
        val response = ApiClient.httpClient.post("/users") {
            contentType(ContentType.Application.Json)
            setBody(createDto)
        }

        return when (response.status) {
            HttpStatusCode.Created, HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para criar usuário")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos ou username já existe")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao criar usuário: ${response.status}")
        }
    }

    /**
     * Atualiza usuário existente
     *
     * Endpoint: PUT /api/users/{id}
     * Permissão: ADMIN ou próprio usuário (campos limitados)
     *
     * @param id ID do usuário
     * @param updateDto Dados a atualizar
     * @return UserDto atualizado
     * @throws Exception se não encontrado (404), sem permissão (403) ou erro
     */
    suspend fun updateUser(id: String, updateDto: UpdateUserDto): UserDto {
        val response = ApiClient.httpClient.put("/users/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Usuário não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar este usuário")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar usuário: ${response.status}")
        }
    }

    /**
     * Remove usuário (soft delete)
     *
     * Endpoint: DELETE /api/users/{id}
     * Permissão necessária: ADMIN apenas
     *
     * ⚠️ SOFT DELETE: Usuário é desativado, não removido fisicamente
     *
     * @param id ID do usuário
     * @throws Exception se não encontrado (404), sem permissão (403) ou erro
     */
    suspend fun deleteUser(id: String) {
        val response = ApiClient.httpClient.delete("/users/$id")

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.NotFound -> throw Exception("Usuário não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para remover usuário")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao remover usuário: ${response.status}")
        }
    }

    /**
     * Ativa usuário desativado
     *
     * Endpoint: PATCH /api/users/{id}/activate
     * Permissão necessária: ADMIN apenas
     *
     * @param id ID do usuário
     * @return UserDto atualizado
     * @throws Exception se não encontrado (404), sem permissão (403) ou erro
     */
    suspend fun activateUser(id: String): UserDto {
        val response = ApiClient.httpClient.patch("/users/$id/activate")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Usuário não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para ativar usuário")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao ativar usuário: ${response.status}")
        }
    }

    /**
     * Desativa usuário ativo
     *
     * Endpoint: PATCH /api/users/{id}/deactivate
     * Permissão necessária: ADMIN apenas
     *
     * @param id ID do usuário
     * @return UserDto atualizado
     * @throws Exception se não encontrado (404), sem permissão (403) ou erro
     */
    suspend fun deactivateUser(id: String): UserDto {
        val response = ApiClient.httpClient.patch("/users/$id/deactivate")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Usuário não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para desativar usuário")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao desativar usuário: ${response.status}")
        }
    }

    /**
     * Atualiza roles do usuário
     *
     * Endpoint: PATCH /api/users/{id}/roles
     * Permissão necessária: ADMIN apenas
     *
     * @param id ID do usuário
     * @param rolesDto Novas roles
     * @return UserDto atualizado
     * @throws Exception se não encontrado (404), sem permissão (403) ou erro
     */
    suspend fun updateUserRoles(id: String, rolesDto: UpdateRolesDto): UserDto {
        val response = ApiClient.httpClient.patch("/users/$id/roles") {
            contentType(ContentType.Application.Json)
            setBody(rolesDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Usuário não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para alterar roles")
            HttpStatusCode.BadRequest -> throw Exception("Roles inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar roles: ${response.status}")
        }
    }

    /**
     * Atualiza senha do usuário
     *
     * Endpoint: PUT /api/users/{id}/password
     * Permissão: Próprio usuário ou ADMIN
     *
     * ⚠️ SEGURANÇA:
     * - Requer senha atual para validação
     * - Nova senha será hasheada no backend
     *
     * @param id ID do usuário
     * @param passwordDto Dados de atualização de senha
     * @throws Exception se senha atual inválida (401), sem permissão (403) ou erro
     */
    suspend fun updatePassword(id: String, passwordDto: UpdatePasswordDto) {
        val response = ApiClient.httpClient.put("/users/$id/password") {
            contentType(ContentType.Application.Json)
            setBody(passwordDto)
        }

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.Unauthorized -> throw Exception("Senha atual incorreta")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para alterar senha")
            HttpStatusCode.BadRequest -> throw Exception("Senha inválida (mínimo 6 caracteres)")
            else -> throw Exception("Erro ao atualizar senha: ${response.status}")
        }
    }
}
