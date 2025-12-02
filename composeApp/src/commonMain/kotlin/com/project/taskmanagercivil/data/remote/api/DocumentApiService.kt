package com.project.taskmanagercivil.data.remote.api

import com.project.taskmanagercivil.data.remote.ApiClient
import com.project.taskmanagercivil.data.remote.dto.document.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

/**
 * Serviço de API para operações com Documentos
 *
 * Endpoints do backend Spring Boot:
 * - GET /api/documents - Lista documentos (com filtros)
 * - GET /api/documents/{id} - Busca documento por ID
 * - GET /api/documents/download/{id} - Download do arquivo binário
 * - POST /api/documents - Cria novo documento
 * - PUT /api/documents/{id} - Atualiza documento
 * - DELETE /api/documents/{id} - Remove documento
 *
 * ⚠️ DOWNLOAD DE ARQUIVOS:
 * O endpoint /download/{id} retorna o arquivo binário (PDF, DWG, DOCX, etc)
 * No WASM, usamos a API do browser para baixar o arquivo
 *
 * ⚠️ DESENVOLVIMENTO (JSON Server):
 * Como JSON Server não serve arquivos binários, usaremos um PDF fake
 * hospedado estaticamente para todos os documentos
 *
 * Permissões por Role:
 * - ADMIN: Acesso total
 * - GESTOR_OBRAS: Gerencia documentos de seus projetos
 * - LIDER_EQUIPE: Adiciona/visualiza documentos
 * - FUNCIONARIO: Visualiza documentos
 */
class DocumentApiService {

    /**
     * Lista documentos com filtros opcionais
     *
     * Endpoint: GET /api/documents
     * Permissão: Todos podem visualizar (filtrado por projeto)
     *
     * @param projectId Filtrar por projeto (opcional)
     * @param category Filtrar por categoria (opcional)
     * @param status Filtrar por status (opcional)
     * @return Lista de DocumentDto
     * @throws Exception se houver erro
     */
    suspend fun getAllDocuments(
        projectId: String? = null,
        category: String? = null,
        status: String? = null
    ): List<DocumentDto> {
        val response = ApiClient.httpClient.get("/documents") {
            url {
                projectId?.let { parameters.append("projectId", it) }
                category?.let { parameters.append("category", it) }
                status?.let { parameters.append("status", it) }
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar documentos: ${response.status}")
        }
    }

    /**
     * Busca documento por ID
     *
     * Endpoint: GET /api/documents/{id}
     * Permissão: Todos podem visualizar
     *
     * @param id ID do documento
     * @return DocumentDto
     * @throws Exception se não encontrado ou erro
     */
    suspend fun getDocumentById(id: String): DocumentDto {
        val response = ApiClient.httpClient.get("/documents/$id")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Documento não encontrado")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar documento: ${response.status}")
        }
    }

    /**
     * Baixa arquivo do documento
     *
     * Endpoint: GET /api/documents/download/{id}
     * Permissão: Todos podem baixar
     *
     * ⚠️ IMPORTANTE: Este método retorna os bytes do arquivo
     * O frontend é responsável por criar o download no browser
     *
     * @param id ID do documento
     * @return ByteArray com conteúdo do arquivo
     * @throws Exception se não encontrado ou erro
     */
    suspend fun downloadDocument(id: String): ByteArray {
        val response = ApiClient.httpClient.get("/documents/download/$id")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Arquivo não encontrado")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao baixar documento: ${response.status}")
        }
    }

    /**
     * Busca versões de um documento
     *
     * Endpoint: GET /api/documents/{id}/versions
     * Permissão: Todos podem visualizar
     *
     * @param documentId ID do documento
     * @return Lista de DocumentVersionDto
     * @throws Exception se houver erro
     */
    suspend fun getDocumentVersions(documentId: String): List<DocumentVersionDto> {
        val response = ApiClient.httpClient.get("/documents/$documentId/versions")

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar versões do documento: ${response.status}")
        }
    }

    /**
     * Busca aprovações de um documento
     *
     * Endpoint: GET /api/documents/{id}/approvals?revision={revision}
     * Permissão: Todos podem visualizar
     *
     * @param documentId ID do documento
     * @param revision Revisão específica (ex: "R00")
     * @return Lista de DocumentApprovalDto
     * @throws Exception se houver erro
     */
    suspend fun getDocumentApprovals(
        documentId: String,
        revision: String
    ): List<DocumentApprovalDto> {
        val response = ApiClient.httpClient.get("/documents/$documentId/approvals") {
            url {
                parameters.append("revision", revision)
            }
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao buscar aprovações do documento: ${response.status}")
        }
    }

    /**
     * Cria novo documento
     *
     * Endpoint: POST /api/documents
     * Permissão necessária: ADICIONAR_DOCUMENTO
     * Roles permitidos: ADMIN, GESTOR_OBRAS, LIDER_EQUIPE
     *
     * @param createDto Dados do novo documento
     * @return DocumentDto criado
     * @throws Exception se sem permissão (403) ou erro de validação (400)
     */
    suspend fun createDocument(createDto: CreateDocumentDto): DocumentDto {
        val response = ApiClient.httpClient.post("/documents") {
            contentType(ContentType.Application.Json)
            setBody(createDto)
        }

        return when (response.status) {
            HttpStatusCode.Created, HttpStatusCode.OK -> response.body()
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para criar documento")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao criar documento: ${response.status}")
        }
    }

    /**
     * Atualiza documento existente
     *
     * Endpoint: PUT /api/documents/{id}
     * Permissão necessária: EDITAR_DOCUMENTO
     * Roles permitidos: ADMIN, GESTOR_OBRAS, LIDER_EQUIPE (próprios)
     *
     * @param id ID do documento
     * @param updateDto Dados a atualizar
     * @return DocumentDto atualizado
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun updateDocument(id: String, updateDto: UpdateDocumentDto): DocumentDto {
        val response = ApiClient.httpClient.put("/documents/$id") {
            contentType(ContentType.Application.Json)
            setBody(updateDto)
        }

        return when (response.status) {
            HttpStatusCode.OK -> response.body()
            HttpStatusCode.NotFound -> throw Exception("Documento não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para editar este documento")
            HttpStatusCode.BadRequest -> throw Exception("Dados inválidos")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao atualizar documento: ${response.status}")
        }
    }

    /**
     * Remove documento
     *
     * Endpoint: DELETE /api/documents/{id}
     * Permissão necessária: REMOVER_DOCUMENTO
     * Roles permitidos: ADMIN, GESTOR_OBRAS
     *
     * @param id ID do documento
     * @throws Exception se sem permissão (403), não encontrado (404) ou erro
     */
    suspend fun deleteDocument(id: String) {
        val response = ApiClient.httpClient.delete("/documents/$id")

        when (response.status) {
            HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                // Sucesso
            }
            HttpStatusCode.NotFound -> throw Exception("Documento não encontrado")
            HttpStatusCode.Forbidden -> throw Exception("Sem permissão para remover este documento")
            HttpStatusCode.Unauthorized -> throw Exception("Não autenticado")
            else -> throw Exception("Erro ao remover documento: ${response.status}")
        }
    }
}
