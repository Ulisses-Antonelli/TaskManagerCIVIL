package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.remote.api.DocumentApiService
import com.project.taskmanagercivil.data.remote.dto.document.*
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Implementação do repositório de documentos com integração ao backend
 *
 * Esta implementação se conecta ao backend via DocumentApiService
 * e faz a conversão entre DTOs (camada de dados) e domain models
 *
 * ⚠️ DOWNLOAD DE ARQUIVOS:
 * O método downloadDocument() retorna ByteArray do arquivo
 * O frontend é responsável por criar o download no browser
 *
 * @param documentApiService Serviço de API para documentos
 */
class DocumentRepositoryImpl(
    private val documentApiService: DocumentApiService = DocumentApiService()
) : DocumentRepository {

    private val _documents = MutableStateFlow<List<Document>>(emptyList())
    private val documents: Flow<List<Document>> = _documents.asStateFlow()

    /**
     * Carrega documentos do backend e atualiza o estado local
     *
     * @param projectId Filtrar por projeto (opcional)
     * @param category Filtrar por categoria (opcional)
     * @param status Filtrar por status (opcional)
     */
    suspend fun loadDocuments(
        projectId: String? = null,
        category: String? = null,
        status: String? = null
    ) {
        try {
            val documentsDto = documentApiService.getAllDocuments(projectId, category, status)
            _documents.value = documentsDto.map { it.toDomainModel() }
        } catch (e: Exception) {
            println("❌ Erro ao carregar documentos: ${e.message}")
            throw e
        }
    }

    override fun getAllDocuments(): Flow<List<Document>> = documents

    override fun getDocumentById(id: String): Document? {
        return _documents.value.find { it.id == id }
    }

    override fun getDocumentsByProject(projectId: String): List<Document> {
        return _documents.value.filter { it.projectId == projectId }
    }

    override fun getDocumentsByCategory(category: DocumentCategory): List<Document> {
        return _documents.value.filter { it.category == category }
    }

    /**
     * Busca versões de um documento do backend
     */
    override fun getDocumentVersions(documentId: String): List<DocumentVersion> {
        // TODO: Implementar busca assíncrona do backend
        // Por enquanto retorna lista vazia
        return emptyList()
    }

    /**
     * Busca aprovações de um documento do backend
     */
    override fun getDocumentApprovals(documentId: String, revision: String): List<DocumentApproval> {
        // TODO: Implementar busca assíncrona do backend
        // Por enquanto retorna lista vazia
        return emptyList()
    }

    /**
     * Adiciona novo documento (método síncrono legado)
     */
    override fun addDocument(document: Document) {
        val currentList = _documents.value.toMutableList()
        currentList.add(document)
        _documents.value = currentList
    }

    /**
     * Adiciona novo documento (versão async que integra com backend)
     *
     * @param document Dados do documento a criar
     * @return Document criado com ID do backend
     * @throws Exception se sem permissão ou erro
     */
    suspend fun addDocumentAsync(document: Document): Document {
        return try {
            val createDto = document.toCreateDto()
            val createdDto = documentApiService.createDocument(createDto)
            val createdDocument = createdDto.toDomainModel()

            // Atualiza cache local
            val currentList = _documents.value.toMutableList()
            currentList.add(createdDocument)
            _documents.value = currentList

            createdDocument
        } catch (e: Exception) {
            println("❌ Erro ao criar documento: ${e.message}")
            throw e
        }
    }

    /**
     * Atualiza documento existente (método síncrono legado)
     */
    override fun updateDocument(document: Document) {
        val currentList = _documents.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == document.id }
        if (index != -1) {
            currentList[index] = document
            _documents.value = currentList
        }
    }

    /**
     * Atualiza documento existente (versão async que integra com backend)
     *
     * @param document Dados do documento a atualizar
     * @return Document atualizado
     * @throws Exception se sem permissão ou erro
     */
    suspend fun updateDocumentAsync(document: Document): Document {
        return try {
            val updateDto = document.toUpdateDto()
            val updatedDto = documentApiService.updateDocument(document.id, updateDto)
            val updatedDocument = updatedDto.toDomainModel()

            // Atualiza cache local
            val currentList = _documents.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == document.id }
            if (index != -1) {
                currentList[index] = updatedDocument
                _documents.value = currentList
            }

            updatedDocument
        } catch (e: Exception) {
            println("❌ Erro ao atualizar documento ${document.id}: ${e.message}")
            throw e
        }
    }

    /**
     * Remove documento (método síncrono legado)
     */
    override fun deleteDocument(id: String) {
        val currentList = _documents.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList.removeAt(index)
            _documents.value = currentList
        }
    }

    /**
     * Remove documento (versão async que integra com backend)
     *
     * @param id ID do documento a remover
     * @throws Exception se sem permissão ou erro
     */
    suspend fun deleteDocumentAsync(id: String) {
        try {
            documentApiService.deleteDocument(id)

            // Remove do cache local
            val currentList = _documents.value.toMutableList()
            val index = currentList.indexOfFirst { it.id == id }
            if (index != -1) {
                currentList.removeAt(index)
                _documents.value = currentList
            }
        } catch (e: Exception) {
            println("❌ Erro ao remover documento $id: ${e.message}")
            throw e
        }
    }

    /**
     * Baixa arquivo do documento
     *
     * Este método busca o arquivo binário do backend
     * O frontend deve usar este ByteArray para criar o download
     *
     * @param id ID do documento
     * @return ByteArray com conteúdo do arquivo
     * @throws Exception se não encontrado ou erro
     */
    override suspend fun downloadDocument(id: String): ByteArray {
        return try {
            documentApiService.downloadDocument(id)
        } catch (e: Exception) {
            println("❌ Erro ao baixar documento $id: ${e.message}")
            throw e
        }
    }

    override fun addVersion(version: DocumentVersion) {
        // TODO: Implementar quando backend estiver pronto
    }

    override fun addApproval(approval: DocumentApproval) {
        // TODO: Implementar quando backend estiver pronto
    }

    override fun updateApproval(approval: DocumentApproval) {
        // TODO: Implementar quando backend estiver pronto
    }

    // ========== Conversões DTO ↔ Domain Model ==========

    private fun DocumentDto.toDomainModel(): Document {
        return Document(
            id = this.id,
            code = this.code,
            title = this.title,
            type = DocumentType.valueOf(this.type),
            category = DocumentCategory.valueOf(this.category),
            discipline = this.discipline?.let { DocumentDiscipline.valueOf(it) },
            taskId = this.taskId,
            projectId = this.projectId,
            phase = ProjectPhase.valueOf(this.phase),
            status = DocumentStatus.valueOf(this.status),
            currentRevision = this.currentRevision,
            createdDate = LocalDate.parse(this.createdDate),
            createdBy = this.createdBy,
            fileUrl = this.fileUrl,
            fileSize = this.fileSize,
            tags = this.tags,
            description = this.description,
            isSuperseded = this.isSuperseded
        )
    }

    private fun Document.toCreateDto(): CreateDocumentDto {
        return CreateDocumentDto(
            code = this.code,
            title = this.title,
            type = this.type.name,
            category = this.category.name,
            discipline = this.discipline?.name,
            taskId = this.taskId,
            projectId = this.projectId,
            phase = this.phase.name,
            currentRevision = this.currentRevision,
            tags = this.tags,
            description = this.description
        )
    }

    private fun Document.toUpdateDto(): UpdateDocumentDto {
        return UpdateDocumentDto(
            title = this.title,
            type = this.type.name,
            status = this.status.name,
            currentRevision = this.currentRevision,
            tags = this.tags,
            description = this.description,
            isSuperseded = this.isSuperseded
        )
    }
}
