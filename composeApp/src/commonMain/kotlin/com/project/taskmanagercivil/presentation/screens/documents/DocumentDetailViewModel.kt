package com.project.taskmanagercivil.presentation.screens.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.ApprovalStatus
import com.project.taskmanagercivil.domain.models.Document
import com.project.taskmanagercivil.domain.models.DocumentApproval
import com.project.taskmanagercivil.domain.models.DocumentVersion
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class DocumentDetailUiState(
    val document: Document? = null,
    val project: Project? = null,
    val creator: Employee? = null,
    val versions: List<DocumentVersion> = emptyList(),
    val approvals: List<DocumentApproval> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class DocumentDetailViewModel(
    private val documentId: String,
    private val documentRepository: DocumentRepository,
    private val projectRepository: ProjectRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentDetailUiState())
    val uiState: StateFlow<DocumentDetailUiState> = _uiState.asStateFlow()

    init {
        loadDocumentDetails()
    }

    private fun loadDocumentDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val document = documentRepository.getDocumentById(documentId)

                if (document != null) {
                    val project: Project? = projectRepository.getProjectById(document.projectId)

                    val creator: Employee? = employeeRepository.getEmployeeById(document.createdBy)

                    val versions: List<DocumentVersion> = documentRepository.getDocumentVersions(document.id)

                    val approvals: List<DocumentApproval> = documentRepository.getDocumentApprovals(
                        document.id,
                        document.currentRevision
                    )

                    _uiState.update {
                        it.copy(
                            document = document,
                            project = project,
                            creator = creator,
                            versions = versions,
                            approvals = approvals,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Documento não encontrado"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados do documento"
                    )
                }
            }
        }
    }

    fun deleteDocument(documentId: String) {
        viewModelScope.launch {
            documentRepository.deleteDocument(documentId)
        }
    }

    fun refreshDocument() {
        loadDocumentDetails()
    }

    fun approveDocument(approvalId: String, status: ApprovalStatus, comments: String?) {
        viewModelScope.launch {
            try {
                val currentApproval = _uiState.value.approvals.find { it.id == approvalId }

                if (currentApproval != null) {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val updatedApproval = currentApproval.copy(
                        status = status,
                        date = now,
                        comments = comments
                    )

                    documentRepository.updateApproval(updatedApproval)

                    refreshDocument()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Erro ao aprovar documento: ${e.message}"
                    )
                }
            }
        }
    }
}
