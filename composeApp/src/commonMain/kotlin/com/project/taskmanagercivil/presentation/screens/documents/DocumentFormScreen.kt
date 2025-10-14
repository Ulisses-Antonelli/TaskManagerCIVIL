package com.project.taskmanagercivil.presentation.screens.documents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentFormScreen(
    viewModel: DocumentFormViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Editar Documento"
                        else "Novo Documento"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Identificação",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.code,
                            onValueChange = { viewModel.onCodeChange(it) },
                            label = { Text("Código") },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                Text(
                                    "Gerado automaticamente se deixado em branco",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.title,
                            onValueChange = { viewModel.onTitleChange(it) },
                            label = { Text("Título *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = uiState.validationErrors.containsKey("title"),
                            supportingText = {
                                uiState.validationErrors["title"]?.let {
                                    Text(it, color = MaterialTheme.colorScheme.error)
                                }
                            },
                            singleLine = true
                        )
                    }

                    item {
                        DocumentTypeDropdownField(
                            label = "Tipo de Documento *",
                            selectedType = uiState.type,
                            onTypeChange = { viewModel.onTypeChange(it) },
                            isError = uiState.validationErrors.containsKey("type"),
                            errorMessage = uiState.validationErrors["type"]
                        )
                    }

                    item {
                        DocumentCategoryDropdownField(
                            label = "Categoria *",
                            selectedCategory = uiState.category,
                            onCategoryChange = { viewModel.onCategoryChange(it) },
                            isError = uiState.validationErrors.containsKey("category"),
                            errorMessage = uiState.validationErrors["category"]
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Classificação",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        DocumentDisciplineDropdownField(
                            label = "Disciplina",
                            selectedDiscipline = uiState.discipline,
                            onDisciplineChange = { viewModel.onDisciplineChange(it) },
                            enabled = uiState.category == DocumentCategory.PLANS_PROJECTS
                        )
                    }

                    item {
                        ProjectPhaseDropdownField(
                            label = "Fase do Projeto *",
                            selectedPhase = uiState.phase,
                            onPhaseChange = { viewModel.onPhaseChange(it) },
                            isError = uiState.validationErrors.containsKey("phase"),
                            errorMessage = uiState.validationErrors["phase"]
                        )
                    }

                    item {
                        DocumentStatusDropdownField(
                            label = "Status *",
                            selectedStatus = uiState.status,
                            onStatusChange = { viewModel.onStatusChange(it) },
                            isError = uiState.validationErrors.containsKey("status"),
                            errorMessage = uiState.validationErrors["status"]
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Revisão e Arquivo",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.currentRevision,
                            onValueChange = { viewModel.onCurrentRevisionChange(it) },
                            label = { Text("Revisão Atual") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.fileUrl,
                            onValueChange = { viewModel.onFileUrlChange(it) },
                            label = { Text("URL do Arquivo") },
                            modifier = Modifier.fillMaxWidth(),
                            supportingText = {
                                Text(
                                    "Caminho ou URL do arquivo PDF/DWG",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.fileSize?.toString() ?: "",
                            onValueChange = {
                                viewModel.onFileSizeChange(it.toLongOrNull())
                            },
                            label = { Text("Tamanho do Arquivo (bytes)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Projeto",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        ProjectDropdownField(
                            label = "Projeto/Obra *",
                            selectedProjectId = uiState.projectId,
                            availableProjects = uiState.availableProjects,
                            onProjectChange = { viewModel.onProjectIdChange(it) },
                            isError = uiState.validationErrors.containsKey("projectId"),
                            errorMessage = uiState.validationErrors["projectId"]
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Descrição",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = { viewModel.onDescriptionChange(it) },
                            label = { Text("Descrição") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tags",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.tags.isNotEmpty()) {
                        items(uiState.tags) { tag ->
                            TagChip(
                                tag = tag,
                                onRemove = { viewModel.onTagRemove(tag) }
                            )
                        }
                    }

                    item {
                        var newTag by remember { mutableStateOf("") }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newTag,
                                onValueChange = { newTag = it },
                                label = { Text("Nova Tag") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    viewModel.onTagAdd(newTag)
                                    newTag = ""
                                },
                                enabled = newTag.isNotBlank()
                            ) {
                                Text("Adicionar")
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = { viewModel.saveDocument(onSaveSuccess) },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSaving
                            ) {
                                if (uiState.isSaving) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                } else {
                                    Text("Salvar")
                                }
                            }
                        }
                    }

                    if (uiState.errorMessage != null) {
                        item {
                            Text(
                                text = uiState.errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentTypeDropdownField(
    label: String,
    selectedType: DocumentType?,
    onTypeChange: (DocumentType?) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedType?.displayName ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = isError,
            supportingText = {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                Row {
                    if (selectedType != null) {
                        TextButton(onClick = { onTypeChange(null) }) {
                            Text("Limpar")
                        }
                    }
                    TextButton(onClick = { expanded = true }) {
                        Text("Selecionar")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DocumentType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.displayName) },
                    onClick = {
                        onTypeChange(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DocumentCategoryDropdownField(
    label: String,
    selectedCategory: DocumentCategory?,
    onCategoryChange: (DocumentCategory?) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedCategory?.displayName ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = isError,
            supportingText = {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                Row {
                    if (selectedCategory != null) {
                        TextButton(onClick = { onCategoryChange(null) }) {
                            Text("Limpar")
                        }
                    }
                    TextButton(onClick = { expanded = true }) {
                        Text("Selecionar")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DocumentCategory.entries.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.displayName) },
                    onClick = {
                        onCategoryChange(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DocumentDisciplineDropdownField(
    label: String,
    selectedDiscipline: DocumentDiscipline?,
    onDisciplineChange: (DocumentDiscipline?) -> Unit,
    enabled: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedDiscipline?.displayName ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            enabled = enabled,
            supportingText = {
                if (!enabled) {
                    Text(
                        "Disponível apenas para documentos de Plantas e Projetos",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            trailingIcon = {
                if (enabled) {
                    Row {
                        if (selectedDiscipline != null) {
                            TextButton(onClick = { onDisciplineChange(null) }) {
                                Text("Limpar")
                            }
                        }
                        TextButton(onClick = { expanded = true }) {
                            Text("Selecionar")
                        }
                    }
                }
            }
        )

        if (enabled) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DocumentDiscipline.entries.forEach { discipline ->
                    DropdownMenuItem(
                        text = { Text(discipline.displayName) },
                        onClick = {
                            onDisciplineChange(discipline)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectPhaseDropdownField(
    label: String,
    selectedPhase: ProjectPhase?,
    onPhaseChange: (ProjectPhase?) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedPhase?.displayName ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = isError,
            supportingText = {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                Row {
                    if (selectedPhase != null) {
                        TextButton(onClick = { onPhaseChange(null) }) {
                            Text("Limpar")
                        }
                    }
                    TextButton(onClick = { expanded = true }) {
                        Text("Selecionar")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ProjectPhase.entries.forEach { phase ->
                DropdownMenuItem(
                    text = { Text(phase.displayName) },
                    onClick = {
                        onPhaseChange(phase)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DocumentStatusDropdownField(
    label: String,
    selectedStatus: DocumentStatus?,
    onStatusChange: (DocumentStatus?) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedStatus?.displayName ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = isError,
            supportingText = {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                Row {
                    if (selectedStatus != null) {
                        TextButton(onClick = { onStatusChange(null) }) {
                            Text("Limpar")
                        }
                    }
                    TextButton(onClick = { expanded = true }) {
                        Text("Selecionar")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DocumentStatus.entries.forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.displayName) },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProjectDropdownField(
    label: String,
    selectedProjectId: String?,
    availableProjects: List<Project>,
    onProjectChange: (String?) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProject = availableProjects.find { it.id == selectedProjectId }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selectedProject?.let { "${it.name} - ${it.location}" } ?: "",
            onValueChange = { },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            isError = isError,
            supportingText = {
                errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            },
            trailingIcon = {
                Row {
                    if (selectedProjectId != null) {
                        TextButton(onClick = { onProjectChange(null) }) {
                            Text("Limpar")
                        }
                    }
                    TextButton(onClick = { expanded = true }) {
                        Text("Selecionar")
                    }
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableProjects.forEach { project ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = project.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = project.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onProjectChange(project.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Remover tag",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
