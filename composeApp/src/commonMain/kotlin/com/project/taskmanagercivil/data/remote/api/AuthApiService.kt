package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.auth.LoginRequestDto
import com.project.taskmanagercivil.data.remote.dto.auth.LoginResponseDto
import com.project.taskmanagercivil.data.remote.dto.auth.RefreshTokenRequestDto
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock

/**
 * Serviço de API para autenticação
 *
 * ⚠️ IMPLEMENTAÇÃO TEMPORÁRIA PARA TESTES COM JSON SERVER
 *
 * JSON Server não suporta lógica de negócio (validação de credenciais),
 * então esta implementação faz validação no cliente temporariamente.
 *
 * TODO: Quando backend Spring Boot estiver pronto, substituir por:
 * - POST /api/auth/login: Login com username/password
 * - POST /api/auth/refresh: Renovar token JWT
 * - POST /api/auth/logout: Fazer logout
 */
class AuthApiService {

    /**
     * Realiza login no backend
     *
     * IMPLEMENTAÇÃO ATUAL (TEMPORÁRIA):
     * 1. Valida credenciais no cliente (hardcoded)
     * 2. Busca dados do usuário via GET /users?username=X
     * 3. Monta resposta de login com token fake
     *
     * TODO: Quando Spring Boot estiver pronto, usar POST /api/auth/login
     *
     * @param username Nome de usuário
     * @param password Senha
     * @return LoginResponseDto com token e dados do usuário
     * @throws Exception se credenciais inválidas ou erro de rede
     */
    suspend fun login(username: String, password: String): LoginResponseDto {
        // ⚠️ MOCK: Validação de credenciais no cliente (temporário)
        val validCredentials = mapOf(
            "admin" to "admin123",
            "manager" to "manager123",
            "joao.silva" to "senha123"
        )

        if (validCredentials[username] != password) {
            throw Exception("Credenciais inválidas")
        }

        // Busca dados do usuário no JSON Server
        val response = ApiClient.httpClient.get("/users") {
            url {
                parameters.append("username", username)
            }
        }

        if (response.status == HttpStatusCode.OK) {
            val users: List<com.project.taskmanagercivil.data.remote.dto.auth.UserDto> = response.body()

            if (users.isEmpty()) {
                throw Exception("Usuário não encontrado no servidor")
            }

            val user = users[0]

            // ⚠️ MOCK: Monta resposta de login com token fake
            val timestamp = Clock.System.now().toEpochMilliseconds()
            val loginResponse = LoginResponseDto(
                accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mock_token_${timestamp}",
                refreshToken = "refresh_${username}_${timestamp}",
                tokenType = "Bearer",
                expiresIn = 3600,
                user = user
            )

            // Armazena o token no ApiClient para requisições futuras
            ApiClient.setAuthToken(loginResponse.accessToken)

            return loginResponse
        } else {
            throw Exception("Erro ao buscar dados do usuário: ${response.status}")
        }
    }

    /**
     * Renova o token JWT usando o refresh token
     *
     * Endpoint: POST /api/auth/refresh
     * Request Body: { refreshToken }
     * Response: { accessToken, refreshToken, tokenType, expiresIn, user }
     *
     * @param refreshToken Token de refresh
     * @return LoginResponseDto com novo token
     * @throws Exception se refresh token inválido ou expirado
     */
    suspend fun refreshToken(refreshToken: String): LoginResponseDto {
        val response = ApiClient.httpClient.post("/api/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequestDto(refreshToken = refreshToken))
        }

        if (response.status == HttpStatusCode.OK) {
            val loginResponse: LoginResponseDto = response.body()

            // Atualiza o token no ApiClient
            ApiClient.setAuthToken(loginResponse.accessToken)

            return loginResponse
        } else {
            throw Exception("Falha ao renovar token: ${response.status}")
        }
    }

    /**
     * Faz logout removendo o token
     *
     * Endpoint: POST /api/auth/logout
     * Headers: Authorization: Bearer {token}
     *
     * Em uma implementação real, o backend invalidaria o token
     * Por enquanto, apenas remove localmente
     */
    suspend fun logout() {
        try {
            // TODO: Chamar endpoint do backend quando estiver pronto
            // ApiClient.httpClient.post("/api/auth/logout")

            // Remove o token localmente
            ApiClient.clearAuthToken()
        } catch (e: Exception) {
            // Mesmo se falhar, remove o token localmente
            ApiClient.clearAuthToken()
            throw e
        }
    }
}
