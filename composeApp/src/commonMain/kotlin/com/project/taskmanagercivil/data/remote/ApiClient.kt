package com.project.taskmanagercivil.data.remote

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Cliente HTTP base para comunicação com o backend Spring Boot
 *
 * Configuração:
 * - Base URL: http://localhost:3000 (JSON Server mock / depois será o Spring Boot)
 * - Content Negotiation: JSON
 * - Logging: Todas as requisições e respostas
 * - Timeout: 30 segundos
 * - JWT Token: Adicionado automaticamente quando disponível
 *
 * WASM Compatible: Usa engine JS automaticamente
 */
object ApiClient {

    /**
     * URL base do backend
     * Em produção, será substituído pela URL real do Spring Boot
     */
    private const val BASE_URL = "http://localhost:3000"

    /**
     * Token JWT armazenado após login
     * TODO: Implementar armazenamento persistente (localStorage/SharedPreferences)
     */
    private var authToken: String? = null

    /**
     * Cliente HTTP configurado com Ktor
     */
    val httpClient = HttpClient {
        // Configuração de JSON
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }

        // Logging de requisições
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        // Timeout padrão
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 30_000
        }

        // Configuração padrão de requisições
        defaultRequest {
            url(BASE_URL)
            contentType(ContentType.Application.Json)

            // Adiciona token JWT se disponível
            authToken?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    /**
     * Define o token JWT após login
     */
    fun setAuthToken(token: String) {
        authToken = token
    }

    /**
     * Remove o token JWT (logout)
     */
    fun clearAuthToken() {
        authToken = null
    }

    /**
     * Obtém o token atual
     */
    fun getAuthToken(): String? = authToken
}
