package com.project.taskmanagercivil.presentation.screens.documents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.Document
import com.project.taskmanagercivil.domain.models.DocumentCategory
import com.project.taskmanagercivil.domain.models.DocumentStatus
import com.project.taskmanagercivil.domain.models.DocumentType
import com.project.taskmanagercivil.domain.models.ProjectPhase
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


data class DocumentsUiState(
    val documents: List<Document> = emptyList(),
    val filteredDocuments: List<Document> = emptyList(),
    val searchQuery: String = "",
    val filterCategory: DocumentFilterCategory = DocumentFilterCategory.ALL,
    val filterType: DocumentType? = null,
    val filterStatus: DocumentStatus? = null,
    val filterPhase: ProjectPhase? = null,
    val sortOrder: DocumentSortOrder = DocumentSortOrder.NAME_ASC,
    val isLoading: Boolean = false
)


enum class DocumentFilterCategory {
    ALL,
    PLANS_PROJECTS,
    TECHNICAL,
    LEGAL_CONTRACTUAL,
    FINANCIAL,
    QUALITY,
    CONSTRUCTION_SITE,
    OTHER
}


enum class DocumentSortOrder(val displayName: String) {
    NAME_ASC("Título (A-Z)"),
    NAME_DESC("Título (Z-A)"),
    DATE_ASC("Data (Mais Antigo)"),
    DATE_DESC("Data (Mais Recente)"),
    CODE_ASC("Código (A-Z)")
}


class DocumentsViewModel(
    private val repository: DocumentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentsUiState())
    val uiState: StateFlow<DocumentsUiState> = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    private fun loadDocuments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getAllDocuments().collect { documents ->
                _uiState.update { currentState ->
                    currentState.copy(
                        documents = documents,
                        filteredDocuments = applyFiltersAndSort(
                            documents,
                            currentState.searchQuery,
                            currentState.filterCategory,
                            currentState.filterType,
                            currentState.filterStatus,
                            currentState.filterPhase,
                            currentState.sortOrder
                        ),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                filteredDocuments = applyFiltersAndSort(
                    currentState.documents,
                    query,
                    currentState.filterCategory,
                    currentState.filterType,
                    currentState.filterStatus,
                    currentState.filterPhase,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onFilterCategoryChange(category: DocumentFilterCategory) {
        _uiState.update { currentState ->
            currentState.copy(
                filterCategory = category,
                filteredDocuments = applyFiltersAndSort(
                    currentState.documents,
                    currentState.searchQuery,
                    category,
                    currentState.filterType,
                    currentState.filterStatus,
                    currentState.filterPhase,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onFilterTypeChange(type: DocumentType?) {
        _uiState.update { currentState ->
            currentState.copy(
                filterType = type,
                filteredDocuments = applyFiltersAndSort(
                    currentState.documents,
                    currentState.searchQuery,
                    currentState.filterCategory,
                    type,
                    currentState.filterStatus,
                    currentState.filterPhase,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onFilterStatusChange(status: DocumentStatus?) {
        _uiState.update { currentState ->
            currentState.copy(
                filterStatus = status,
                filteredDocuments = applyFiltersAndSort(
                    currentState.documents,
                    currentState.searchQuery,
                    currentState.filterCategory,
                    currentState.filterType,
                    status,
                    currentState.filterPhase,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onFilterPhaseChange(phase: ProjectPhase?) {
        _uiState.update { currentState ->
            currentState.copy(
                filterPhase = phase,
                filteredDocuments = applyFiltersAndSort(
                    currentState.documents,
                    currentState.searchQuery,
                    currentState.filterCategory,
                    currentState.filterType,
                    currentState.filterStatus,
                    phase,
                    currentState.sortOrder
                )
            )
        }
    }

    fun onSortOrderChange(sortOrder: DocumentSortOrder) {
        _uiState.update { currentState ->
            currentState.copy(
                sortOrder = sortOrder,
                filteredDocuments = applyFiltersAndSort(
                    currentState.documents,
                    currentState.searchQuery,
                    currentState.filterCategory,
                    currentState.filterType,
                    currentState.filterStatus,
                    currentState.filterPhase,
                    sortOrder
                )
            )
        }
    }

    private fun applyFiltersAndSort(
        documents: List<Document>,
        searchQuery: String,
        filterCategory: DocumentFilterCategory,
        filterType: DocumentType?,
        filterStatus: DocumentStatus?,
        filterPhase: ProjectPhase?,
        sortOrder: DocumentSortOrder
    ): List<Document> {
        var result = documents

        result = when (filterCategory) {
            DocumentFilterCategory.ALL -> result
            DocumentFilterCategory.PLANS_PROJECTS -> result.filter { it.category == DocumentCategory.PLANS_PROJECTS }
            DocumentFilterCategory.TECHNICAL -> result.filter { it.category == DocumentCategory.TECHNICAL }
            DocumentFilterCategory.LEGAL_CONTRACTUAL -> result.filter { it.category == DocumentCategory.LEGAL_CONTRACTUAL }
            DocumentFilterCategory.FINANCIAL -> result.filter { it.category == DocumentCategory.FINANCIAL }
            DocumentFilterCategory.QUALITY -> result.filter { it.category == DocumentCategory.QUALITY }
            DocumentFilterCategory.CONSTRUCTION_SITE -> result.filter { it.category == DocumentCategory.CONSTRUCTION_SITE }
            DocumentFilterCategory.OTHER -> result.filter { it.category == DocumentCategory.OTHER }
        }

        if (filterType != null) {
            result = result.filter { document: Document -> document.type == filterType }
        }

        if (filterStatus != null) {
            result = result.filter { document: Document -> document.status == filterStatus }
        }

        if (filterPhase != null) {
            result = result.filter { document: Document -> document.phase == filterPhase }
        }

        if (searchQuery.isNotBlank()) {
            result = result.filter { document ->
                document.title.contains(searchQuery, ignoreCase = true) ||
                document.code.contains(searchQuery, ignoreCase = true) ||
                document.description?.contains(searchQuery, ignoreCase = true) == true
            }
        }

        result = when (sortOrder) {
            DocumentSortOrder.NAME_ASC -> result.sortedBy { it.title }
            DocumentSortOrder.NAME_DESC -> result.sortedByDescending { it.title }
            DocumentSortOrder.DATE_ASC -> result.sortedBy { it.createdDate }
            DocumentSortOrder.DATE_DESC -> result.sortedByDescending { it.createdDate }
            DocumentSortOrder.CODE_ASC -> result.sortedBy { it.code }
        }

        return result
    }

    fun deleteDocument(documentId: String) {
        viewModelScope.launch {
            repository.deleteDocument(documentId)
        }
    }
}
