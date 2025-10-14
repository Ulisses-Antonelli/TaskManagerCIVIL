package com.project.taskmanagercivil.presentation.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.utils.FormatUtils

@Composable
fun DocumentsScreenContent(
    viewModel: DocumentsViewModel,
    onDocumentClick: (String) -> Unit,
    onCreateDocument: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "documents",
            onMenuClick = onNavigate
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Documentos",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onCreateDocument,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("+ Novo Documento")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            DocumentFiltersSection(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                filterCategory = uiState.filterCategory,
                onFilterCategoryChange = { viewModel.onFilterCategoryChange(it) },
                filterType = uiState.filterType,
                onFilterTypeChange = { viewModel.onFilterTypeChange(it) },
                filterStatus = uiState.filterStatus,
                onFilterStatusChange = { viewModel.onFilterStatusChange(it) },
                filterPhase = uiState.filterPhase,
                onFilterPhaseChange = { viewModel.onFilterPhaseChange(it) },
                sortOrder = uiState.sortOrder,
                onSortOrderChange = { viewModel.onSortOrderChange(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredDocuments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum documento encontrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredDocuments,
                        key = { it.id }
                    ) { document ->
                        DocumentCard(
                            document = document,
                            onClick = { onDocumentClick(document.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentFiltersSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filterCategory: DocumentFilterCategory,
    onFilterCategoryChange: (DocumentFilterCategory) -> Unit,
    filterType: DocumentType?,
    onFilterTypeChange: (DocumentType?) -> Unit,
    filterStatus: DocumentStatus?,
    onFilterStatusChange: (DocumentStatus?) -> Unit,
    filterPhase: ProjectPhase?,
    onFilterPhaseChange: (ProjectPhase?) -> Unit,
    sortOrder: DocumentSortOrder,
    onSortOrderChange: (DocumentSortOrder) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por título, código ou descrição...") },
                singleLine = true
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (filterCategory) {
                                DocumentFilterCategory.ALL -> "Todas as Categorias"
                                DocumentFilterCategory.PLANS_PROJECTS -> DocumentCategory.PLANS_PROJECTS.displayName
                                DocumentFilterCategory.TECHNICAL -> DocumentCategory.TECHNICAL.displayName
                                DocumentFilterCategory.LEGAL_CONTRACTUAL -> DocumentCategory.LEGAL_CONTRACTUAL.displayName
                                DocumentFilterCategory.FINANCIAL -> DocumentCategory.FINANCIAL.displayName
                                DocumentFilterCategory.QUALITY -> DocumentCategory.QUALITY.displayName
                                DocumentFilterCategory.CONSTRUCTION_SITE -> DocumentCategory.CONSTRUCTION_SITE.displayName
                                DocumentFilterCategory.OTHER -> DocumentCategory.OTHER.displayName
                            }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas as Categorias") },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.ALL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.PLANS_PROJECTS.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.PLANS_PROJECTS)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.TECHNICAL.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.TECHNICAL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.LEGAL_CONTRACTUAL.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.LEGAL_CONTRACTUAL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.FINANCIAL.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.FINANCIAL)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.QUALITY.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.QUALITY)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.CONSTRUCTION_SITE.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.CONSTRUCTION_SITE)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(DocumentCategory.OTHER.displayName) },
                            onClick = {
                                onFilterCategoryChange(DocumentFilterCategory.OTHER)
                                expanded = false
                            }
                        )
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = filterType?.displayName ?: "Todos os Tipos")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos os Tipos") },
                            onClick = {
                                onFilterTypeChange(null)
                                expanded = false
                            }
                        )
                        DocumentType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    onFilterTypeChange(type)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = filterStatus?.displayName ?: "Todos os Status")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todos os Status") },
                            onClick = {
                                onFilterStatusChange(null)
                                expanded = false
                            }
                        )
                        DocumentStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.displayName) },
                                onClick = {
                                    onFilterStatusChange(status)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = filterPhase?.displayName ?: "Todas as Fases")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas as Fases") },
                            onClick = {
                                onFilterPhaseChange(null)
                                expanded = false
                            }
                        )
                        ProjectPhase.entries.forEach { phase ->
                            DropdownMenuItem(
                                text = { Text(phase.displayName) },
                                onClick = {
                                    onFilterPhaseChange(phase)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    var expanded by remember { mutableStateOf(false) }

                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = sortOrder.displayName)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DocumentSortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.displayName) },
                                onClick = {
                                    onSortOrderChange(order)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: Document,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(getCategoryColor(document.category)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryIcon(document.category),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = document.code,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = document.type.displayName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Surface(
                        color = getStatusColor(document.status),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = document.status.displayName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rev. ${document.currentRevision}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = FormatUtils.formatDate(document.createdDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (document.fileSize != null) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatFileSize(document.fileSize),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getCategoryColor(category: DocumentCategory): Color {
    return when (category) {
        DocumentCategory.PLANS_PROJECTS -> Color(0xFF2196F3)
        DocumentCategory.TECHNICAL -> Color(0xFF9C27B0)
        DocumentCategory.LEGAL_CONTRACTUAL -> Color(0xFF795548)
        DocumentCategory.FINANCIAL -> Color(0xFF4CAF50)
        DocumentCategory.QUALITY -> Color(0xFFFFC107)
        DocumentCategory.CONSTRUCTION_SITE -> Color(0xFFFF5722)
        DocumentCategory.OTHER -> Color(0xFF607D8B)
    }
}

private fun getCategoryIcon(category: DocumentCategory): String {
    return when (category) {
        DocumentCategory.PLANS_PROJECTS -> "📐"
        DocumentCategory.TECHNICAL -> "📋"
        DocumentCategory.LEGAL_CONTRACTUAL -> "📜"
        DocumentCategory.FINANCIAL -> "💰"
        DocumentCategory.QUALITY -> "✓"
        DocumentCategory.CONSTRUCTION_SITE -> "🏗"
        DocumentCategory.OTHER -> "📄"
    }
}

@Composable
private fun getStatusColor(status: DocumentStatus): Color {
    return when (status) {
        DocumentStatus.APPROVED -> Color(0xFF4CAF50)
        DocumentStatus.IN_PROGRESS -> Color(0xFF2196F3)
        DocumentStatus.FOR_REVIEW -> Color(0xFFFFC107)
        DocumentStatus.IN_APPROVAL -> Color(0xFFFF9800)
        DocumentStatus.REJECTED -> Color(0xFFF44336)
        DocumentStatus.SUPERSEDED -> Color(0xFF9E9E9E)
        DocumentStatus.ARCHIVED -> Color(0xFF757575)
    }
}

private fun formatFileSize(bytes: Long): String {
    val kb = bytes / 1024.0
    val mb = kb / 1024.0

    return when {
        mb >= 1 -> {
            val rounded = (mb * 10).toLong() / 10.0
            "$rounded MB"
        }
        kb >= 1 -> {
            val rounded = (kb * 10).toLong() / 10.0
            "$rounded KB"
        }
        else -> "$bytes B"
    }
}
