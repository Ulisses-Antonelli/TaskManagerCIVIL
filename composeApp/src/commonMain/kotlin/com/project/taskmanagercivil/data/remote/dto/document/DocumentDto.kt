package com.project.taskmanagercivil.data.remote.dto.document

import kotlinx.serialization.Serializable

/**
 * DTOs para Documentos - compatível com Spring Boot backend
 *
 * Endpoints do backend:
 * - GET /api/documents - Lista documentos (com filtros)
 * - GET /api/documents/{id} - Busca documento por ID
 * - GET /api/documents/download/{id} - Download do arquivo
 * - POST /api/documents - Cria novo documento
 * - PUT /api/documents/{id} - Atualiza documento
 * - DELETE /api/documents/{id} - Remove documento
 *
 * ⚠️ IMPORTANTE: Download de arquivos usa endpoint separado
 * O backend retorna o arquivo binário (PDF, DWG, etc)
 * Para desenvolvimento, usaremos um arquivo fake para todos
 *
 * Permissões por Role:
 * - ADMIN: Acesso total
 * - GESTOR_OBRAS: Pode gerenciar documentos de seus projetos
 * - LIDER_EQUIPE: Pode adicionar/visualizar documentos
 * - FUNCIONARIO: Pode visualizar documentos
 */

@Serializable
data class DocumentDto(
    val id: String,
    val code: String,  // OBRA-DISC-SEQ-REV (ex: VV-ARQ-001-R02)
    val title: String,
    val type: String,  // DocumentType enum value
    val category: String,  // DocumentCategory enum value
    val discipline: String? = null,  // DocumentDiscipline enum value
    val taskId: String,
    val projectId: String,
    val phase: String,  // ProjectPhase enum value
    val status: String,  // DocumentStatus enum value
    val currentRevision: String,  // R00, R01, etc
    val createdDate: String,  // ISO 8601: "2024-01-01"
    val createdBy: String,
    val fileUrl: String? = null,  // URL relativa para download
    val fileSize: Long? = null,  // Tamanho em bytes
    val tags: List<String> = emptyList(),
    val description: String? = null,
    val isSuperseded: Boolean = false,
    val createdAt: String,  // ISO 8601: "2024-01-01T00:00:00Z"
    val updatedAt: String,
    val updatedBy: String? = null
)

@Serializable
data class DocumentVersionDto(
    val id: String,
    val documentId: String,
    val revision: String,
    val versionDate: String,  // ISO 8601: "2024-01-01"
    val createdBy: String,
    val changes: String,
    val fileUrl: String? = null,
    val status: String,  // DocumentStatus enum value
    val isSuperseded: Boolean = false
)

@Serializable
data class DocumentApprovalDto(
    val id: String,
    val documentId: String,
    val revision: String,
    val stage: String,  // ApprovalStage enum value
    val approver: String,
    val approverName: String,
    val approverRole: String,
    val status: String,  // ApprovalStatus enum value
    val date: String? = null,  // ISO 8601: "2024-01-01T10:30:00Z"
    val comments: String? = null
)

/**
 * DTO para criar documento (sem campos auto-gerados)
 */
@Serializable
data class CreateDocumentDto(
    val code: String,
    val title: String,
    val type: String,
    val category: String,
    val discipline: String? = null,
    val taskId: String,
    val projectId: String,
    val phase: String,
    val currentRevision: String = "R00",
    val tags: List<String> = emptyList(),
    val description: String? = null
)

/**
 * DTO para atualizar documento
 */
@Serializable
data class UpdateDocumentDto(
    val title: String? = null,
    val type: String? = null,
    val status: String? = null,
    val currentRevision: String? = null,
    val tags: List<String>? = null,
    val description: String? = null,
    val isSuperseded: Boolean? = null
)
