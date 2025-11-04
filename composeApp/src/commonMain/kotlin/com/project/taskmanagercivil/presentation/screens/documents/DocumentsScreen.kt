package com.project.taskmanagercivil.presentation.screens.documents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.*
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsScreenContent(
    navController: NavController,
    viewModel: DocumentsViewModel,
    onDocumentClick: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    val authViewModel = com.project.taskmanagercivil.presentation.ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Controla se o logout foi solicitado
    var logoutRequested by remember { mutableStateOf(false) }

    // Observa mudanças no estado de autenticação
    LaunchedEffect(authState.currentUser) {
        if (logoutRequested && authState.currentUser == null) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
            logoutRequested = false
        }
    }

    // Estado do modal
    var showDocumentFormModal by remember { mutableStateOf(false) }
    var documentToEdit by remember { mutableStateOf<Document?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "documents",
            onMenuClick = onNavigate
        )

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.weight(1f)) {
            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "TaskManagerCIVIL",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            actions = {
                                // Avatar do usuário com menu
                                com.project.taskmanagercivil.presentation.components.UserMenuAvatar(
                                    user = authState.currentUser,
                                    onLogout = {
                                        logoutRequested = true
                                        authViewModel.logout()
                                    },
                                    onSettings = {
                                        navController.navigate("settings") {
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DynamicBreadcrumbs(
                                navController = navController,
                                currentRoot = NavigationState.currentRoot,
                                modifier = Modifier.weight(1f)
                            )

                            // Botão de adicionar
                            IconButton(
                                onClick = {
                                    documentToEdit = null
                                    showDocumentFormModal = true
                                },
                                modifier = Modifier.size(40.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Criar novo documento",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Barra de pesquisa
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Filtros
                    FiltersRow(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Lista de documentos
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        uiState.filteredDocuments.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nenhum documento encontrado",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tente ajustar os filtros",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.filteredDocuments,
                                    key = { it.id }
                                ) { document ->
                                    DocumentCard(
                                        document = document,
                                        onClick = { onDocumentClick(document.id) },
                                        onEdit = {
                                            documentToEdit = document
                                            showDocumentFormModal = true
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Modal de formulário de documento
                    if (showDocumentFormModal) {
                        DocumentFormModal(
                            document = documentToEdit,
                            availableProjects = uiState.allProjects,
                            availableTasks = uiState.allTasks,
                            onDismiss = {
                                showDocumentFormModal = false
                                documentToEdit = null
                            },
                            onSave = { document ->
                                viewModel.saveDocument(document)
                                showDocumentFormModal = false
                                documentToEdit = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Buscar por título, código ou descrição...") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersRow(
    uiState: DocumentsUiState,
    viewModel: DocumentsViewModel,
    modifier: Modifier = Modifier
) {
    // Estados para controlar abertura dos dropdowns
    var categoryExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var phaseExpanded by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Primeira linha: Categoria, Tipo, Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown 1: Categoria
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = when (uiState.filterCategory) {
                        DocumentFilterCategory.ALL -> "Todas as Categorias"
                        DocumentFilterCategory.PLANS_PROJECTS -> DocumentCategory.PLANS_PROJECTS.displayName
                        DocumentFilterCategory.TECHNICAL -> DocumentCategory.TECHNICAL.displayName
                        DocumentFilterCategory.LEGAL_CONTRACTUAL -> DocumentCategory.LEGAL_CONTRACTUAL.displayName
                        DocumentFilterCategory.FINANCIAL -> DocumentCategory.FINANCIAL.displayName
                        DocumentFilterCategory.QUALITY -> DocumentCategory.QUALITY.displayName
                        DocumentFilterCategory.CONSTRUCTION_SITE -> DocumentCategory.CONSTRUCTION_SITE.displayName
                        DocumentFilterCategory.OTHER -> DocumentCategory.OTHER.displayName
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas as Categorias") },
                        onClick = {
                            viewModel.onFilterCategoryChange(DocumentFilterCategory.ALL)
                            categoryExpanded = false
                        }
                    )
                    DocumentFilterCategory.entries.forEach { filter ->
                        if (filter != DocumentFilterCategory.ALL) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (filter) {
                                            DocumentFilterCategory.PLANS_PROJECTS -> DocumentCategory.PLANS_PROJECTS.displayName
                                            DocumentFilterCategory.TECHNICAL -> DocumentCategory.TECHNICAL.displayName
                                            DocumentFilterCategory.LEGAL_CONTRACTUAL -> DocumentCategory.LEGAL_CONTRACTUAL.displayName
                                            DocumentFilterCategory.FINANCIAL -> DocumentCategory.FINANCIAL.displayName
                                            DocumentFilterCategory.QUALITY -> DocumentCategory.QUALITY.displayName
                                            DocumentFilterCategory.CONSTRUCTION_SITE -> DocumentCategory.CONSTRUCTION_SITE.displayName
                                            DocumentFilterCategory.OTHER -> DocumentCategory.OTHER.displayName
                                            else -> ""
                                        }
                                    )
                                },
                                onClick = {
                                    viewModel.onFilterCategoryChange(filter)
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Dropdown 2: Tipo de Documento
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.filterType?.displayName ?: "Todos os Tipos",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos os Tipos") },
                        onClick = {
                            viewModel.onFilterTypeChange(null)
                            typeExpanded = false
                        }
                    )
                    DocumentType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                viewModel.onFilterTypeChange(type)
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown 3: Status do Documento
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.filterStatus?.displayName ?: "Todos os Status",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos os Status") },
                        onClick = {
                            viewModel.onFilterStatusChange(null)
                            statusExpanded = false
                        }
                    )
                    DocumentStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.displayName) },
                            onClick = {
                                viewModel.onFilterStatusChange(status)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Segunda linha: Fase, Ordenação
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown 4: Fase do Documento
            ExposedDropdownMenuBox(
                expanded = phaseExpanded,
                onExpandedChange = { phaseExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.filterPhase?.displayName ?: "Todas as Fases",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fase") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = phaseExpanded,
                    onDismissRequest = { phaseExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas as Fases") },
                        onClick = {
                            viewModel.onFilterPhaseChange(null)
                            phaseExpanded = false
                        }
                    )
                    ProjectPhase.entries.forEach { phase ->
                        DropdownMenuItem(
                            text = { Text(phase.displayName) },
                            onClick = {
                                viewModel.onFilterPhaseChange(phase)
                                phaseExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown 5: Ordenação
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.sortOrder.displayName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ordenar por") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    DocumentSortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.displayName) },
                            onClick = {
                                viewModel.onSortOrderChange(order)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Botão para limpar filtros (se houver filtros ativos)
        if (uiState.filterCategory != DocumentFilterCategory.ALL ||
            uiState.filterType != null ||
            uiState.filterStatus != null ||
            uiState.filterPhase != null
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    viewModel.onFilterCategoryChange(DocumentFilterCategory.ALL)
                    viewModel.onFilterTypeChange(null)
                    viewModel.onFilterStatusChange(null)
                    viewModel.onFilterPhaseChange(null)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Limpar Filtros")
            }
        }
    }
}

@Composable
private fun DocumentCard(
    document: Document,
    onClick: () -> Unit,
    onEdit: ((Document) -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar com ícone da categoria
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

            // Coluna 1: Código (maior) e Título
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = document.code,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Divisor vertical 1
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 2: Time/Disciplina
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Time",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = document.discipline?.displayName ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }

            // Divisor vertical 2
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 3: Tipo
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tipo",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = document.type.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Divisor vertical 3
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 4: Status
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = getStatusColor(document.status),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = document.status.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            // Divisor vertical 4
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 5: Revisão e Data
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rev. ${document.currentRevision}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = FormatUtils.formatDate(document.createdDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (document.fileSize != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatFileSize(document.fileSize),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

            // Botão Editar no canto superior direito (sobreposto)
            if (onEdit != null) {
                var showMenu by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar") },
                            onClick = {
                                showMenu = false
                                onEdit(document)
                            }
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
