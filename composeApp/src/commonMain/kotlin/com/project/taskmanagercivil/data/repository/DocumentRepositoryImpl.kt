package com.project.taskmanagercivil.data.repository

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Document
import com.project.taskmanagercivil.domain.models.DocumentApproval
import com.project.taskmanagercivil.domain.models.DocumentCategory
import com.project.taskmanagercivil.domain.models.DocumentVersion
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Implementação do repositório de documentos usando dados mockados
 */
class DocumentRepositoryImpl : DocumentRepository {
    private val mockData = MockData()
    private val _documents = MutableStateFlow(mockData.documents)
    private val documents: Flow<List<Document>> = _documents.asStateFlow()

    private val _versions = MutableStateFlow(mockData.documentVersions)
    private val _approvals = MutableStateFlow(mockData.documentApprovals)

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

    override fun getDocumentVersions(documentId: String): List<DocumentVersion> {
        return _versions.value.filter { it.documentId == documentId }
    }

    override fun getDocumentApprovals(documentId: String, revision: String): List<DocumentApproval> {
        return _approvals.value.filter {
            it.documentId == documentId && it.revision == revision
        }
    }

    override fun addDocument(document: Document) {
        val currentList = _documents.value.toMutableList()
        currentList.add(document)
        _documents.value = currentList
    }

    override fun updateDocument(document: Document) {
        val currentList = _documents.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == document.id }
        if (index != -1) {
            currentList[index] = document
            _documents.value = currentList
        }
    }

    override fun deleteDocument(id: String) {
        val currentList = _documents.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList.removeAt(index)
            _documents.value = currentList
        }
    }

    override fun addVersion(version: DocumentVersion) {
        val currentList = _versions.value.toMutableList()
        currentList.add(version)
        _versions.value = currentList
    }

    override fun addApproval(approval: DocumentApproval) {
        val currentList = _approvals.value.toMutableList()
        currentList.add(approval)
        _approvals.value = currentList
    }

    override fun updateApproval(approval: DocumentApproval) {
        val currentList = _approvals.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == approval.id }
        if (index != -1) {
            currentList[index] = approval
            _approvals.value = currentList
        }
    }
}
