package com.project.taskmanagercivil.presentation.screens.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Document
import com.project.taskmanagercivil.domain.models.DocumentCategory
import com.project.taskmanagercivil.domain.models.DocumentDiscipline
import com.project.taskmanagercivil.domain.models.DocumentStatus
import com.project.taskmanagercivil.domain.models.DocumentType
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.ProjectPhase
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


data class DocumentFormUiState(
    val documentId: String? = null,
    val code: String = "",
    val title: String = "",
    val type: DocumentType? = null,
    val category: DocumentCategory? = null,
    val discipline: DocumentDiscipline? = null,
    val taskId: String? = null,
    val projectId: String? = null,
    val phase: ProjectPhase? = null,
    val status: DocumentStatus? = null,
    val currentRevision: String = "R00",
    val createdDate: LocalDate? = null,
    val fileUrl: String = "",
    val fileSize: Long? = null,
    val tags: List<String> = emptyList(),
    val description: String = "",
    val availableProjects: List<Project> = emptyList(),
    val availableEmployees: List<Employee> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val validationErrors: Map<String, String> = emptyMap()
) {
    val isEditMode: Boolean get() = documentId != null
}


class DocumentFormViewModel(
    private val documentId: String?,
    private val documentRepository: DocumentRepository,
    private val projectRepository: ProjectRepository,
    private val employeeRepository: EmployeeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentFormUiState(documentId = documentId))
    val uiState: StateFlow<DocumentFormUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val projects: List<Project> = projectRepository.getAllProjects()
                _uiState.update { it.copy(availableProjects = projects) }

                val employees: List<Employee> = employeeRepository.getAllEmployees().first()
                _uiState.update { it.copy(availableEmployees = employees) }

                if (documentId != null) {
                    val document = documentRepository.getDocumentById(documentId)
                    if (document != null) {
                        _uiState.update {
                            it.copy(
                                code = document.code,
                                title = document.title,
                                type = document.type,
                                category = document.category,
                                discipline = document.discipline,
                                taskId = document.taskId,
                                projectId = document.projectId,
                                phase = document.phase,
                                status = document.status,
                                currentRevision = document.currentRevision,
                                createdDate = document.createdDate,
                                fileUrl = document.fileUrl ?: "",
                                fileSize = document.fileSize,
                                tags = document.tags,
                                description = document.description ?: "",
                                isLoading = false
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
                } else {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    _uiState.update {
                        it.copy(
                            createdDate = now.date,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar dados"
                    )
                }
            }
        }
    }

    fun onCodeChange(value: String) {
        _uiState.update { it.copy(code = value) }
        clearFieldError("code")
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value) }
        clearFieldError("title")
    }

    fun onTypeChange(value: DocumentType?) {
        _uiState.update { it.copy(type = value) }
        clearFieldError("type")
    }

    fun onCategoryChange(value: DocumentCategory?) {
        _uiState.update { it.copy(category = value) }
        clearFieldError("category")
    }

    fun onDisciplineChange(value: DocumentDiscipline?) {
        _uiState.update { it.copy(discipline = value) }
    }

    fun onProjectIdChange(value: String?) {
        _uiState.update { it.copy(projectId = value) }
        clearFieldError("projectId")
    }

    fun onPhaseChange(value: ProjectPhase?) {
        _uiState.update { it.copy(phase = value) }
        clearFieldError("phase")
    }

    fun onStatusChange(value: DocumentStatus?) {
        _uiState.update { it.copy(status = value) }
        clearFieldError("status")
    }

    fun onCurrentRevisionChange(value: String) {
        _uiState.update { it.copy(currentRevision = value) }
    }

    fun onCreatedDateChange(date: LocalDate?) {
        _uiState.update { it.copy(createdDate = date) }
    }

    fun onFileUrlChange(value: String) {
        _uiState.update { it.copy(fileUrl = value) }
    }

    fun onFileSizeChange(value: Long?) {
        _uiState.update { it.copy(fileSize = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onTagAdd(tag: String) {
        _uiState.update { currentState ->
            val trimmedTag = tag.trim()
            if (trimmedTag.isNotBlank() && !currentState.tags.contains(trimmedTag)) {
                currentState.copy(tags = currentState.tags + trimmedTag)
            } else {
                currentState
            }
        }
    }

    fun onTagRemove(tag: String) {
        _uiState.update { currentState ->
            currentState.copy(tags = currentState.tags - tag)
        }
    }

    private fun clearFieldError(field: String) {
        _uiState.update { currentState ->
            currentState.copy(
                validationErrors = currentState.validationErrors - field
            )
        }
    }

    fun saveDocument(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        val errors = mutableMapOf<String, String>()

        if (currentState.title.isBlank()) {
            errors["title"] = "Título é obrigatório"
        }

        if (currentState.type == null) {
            errors["type"] = "Tipo de documento é obrigatório"
        }

        if (currentState.category == null) {
            errors["category"] = "Categoria é obrigatória"
        }

        if (currentState.projectId == null) {
            errors["projectId"] = "Projeto é obrigatório"
        }

        if (currentState.phase == null) {
            errors["phase"] = "Fase do projeto é obrigatória"
        }

        if (currentState.status == null) {
            errors["status"] = "Status é obrigatório"
        }

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = errors) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val finalCode = if (currentState.code.isBlank()) {
                    generateDocumentCode(
                        projectId = currentState.projectId!!,
                        discipline = currentState.discipline,
                        revision = currentState.currentRevision
                    )
                } else {
                    currentState.code.trim()
                }

                val creatorId = if (documentId != null) {
                    val existingDocument = documentRepository.getDocumentById(documentId)
                    existingDocument?.createdBy ?: "1"
                } else {
                    "1"
                }

                val document = Document(
                    id = documentId ?: generateId(),
                    code = finalCode,
                    title = currentState.title.trim(),
                    type = currentState.type!!,
                    category = currentState.category!!,
                    discipline = currentState.discipline,
                    taskId = currentState.taskId ?: "1", // TODO: Tornar obrigatório no formulário
                    projectId = currentState.projectId!!,
                    phase = currentState.phase!!,
                    status = currentState.status!!,
                    currentRevision = currentState.currentRevision.trim(),
                    createdDate = currentState.createdDate!!,
                    createdBy = creatorId,
                    fileUrl = currentState.fileUrl.trim().ifBlank { null },
                    fileSize = currentState.fileSize,
                    tags = currentState.tags,
                    description = currentState.description.trim().ifBlank { null },
                    isSuperseded = false
                )

                if (documentId != null) {
                    documentRepository.updateDocument(document)
                } else {
                    documentRepository.addDocument(document)
                }

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "Erro ao salvar documento: ${e.message}"
                    )
                }
            }
        }
    }

    private fun generateDocumentCode(
        projectId: String,
        discipline: DocumentDiscipline?,
        revision: String
    ): String {
        val project = _uiState.value.availableProjects.find { it.id == projectId }
        val projectCode = project?.name?.take(2)?.uppercase() ?: "XX"

        val disciplineCode = discipline?.code ?: "DOC"

        val randomNumber = (1..999).random()
        val sequentialNumber = randomNumber.toString().padStart(3, '0')

        return "$projectCode-$disciplineCode-$sequentialNumber-$revision"
    }

    private fun generateId(): String {
        return Clock.System.now().toEpochMilliseconds().toString()
    }
}
