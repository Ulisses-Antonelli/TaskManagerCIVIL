package com.project.taskmanagercivil.domain.repository

import com.project.taskmanagercivil.domain.models.Document
import com.project.taskmanagercivil.domain.models.DocumentApproval
import com.project.taskmanagercivil.domain.models.DocumentCategory
import com.project.taskmanagercivil.domain.models.DocumentVersion
import kotlinx.coroutines.flow.Flow

/**
 * Interface de repositório para operações CRUD de documentos
 */
interface DocumentRepository {
    fun getAllDocuments(): Flow<List<Document>>
    fun getDocumentById(id: String): Document?
    fun getDocumentsByProject(projectId: String): List<Document>
    fun getDocumentsByCategory(category: DocumentCategory): List<Document>
    fun getDocumentVersions(documentId: String): List<DocumentVersion>
    fun getDocumentApprovals(documentId: String, revision: String): List<DocumentApproval>
    fun addDocument(document: Document)
    fun updateDocument(document: Document)
    fun deleteDocument(id: String)
    fun addVersion(version: DocumentVersion)
    fun addApproval(approval: DocumentApproval)
    fun updateApproval(approval: DocumentApproval)

    /**
     * Baixa arquivo do documento
     * @param id ID do documento
     * @return ByteArray com conteúdo do arquivo
     */
    suspend fun downloadDocument(id: String): ByteArray
}
