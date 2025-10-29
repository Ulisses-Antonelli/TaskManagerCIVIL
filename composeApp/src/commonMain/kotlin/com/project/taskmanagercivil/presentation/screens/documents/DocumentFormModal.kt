package com.project.taskmanagercivil.presentation.screens.documents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentFormModal(
    document: Document? = null,
    availableProjects: List<Project> = emptyList(),
    availableTasks: List<Task> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (Document) -> Unit
) {
    // Estados do formulário
    var code by remember { mutableStateOf(document?.code ?: "") }
    var title by remember { mutableStateOf(document?.title ?: "") }
    var type by remember { mutableStateOf(document?.type) }
    var category by remember { mutableStateOf(document?.category) }
    var discipline by remember { mutableStateOf(document?.discipline) }
    var selectedProjectId by remember { mutableStateOf(document?.projectId) }
    var selectedTaskId by remember { mutableStateOf(document?.taskId) }
    var phase by remember { mutableStateOf(document?.phase ?: ProjectPhase.BASIC_PROJECT) }
    var status by remember { mutableStateOf(document?.status ?: DocumentStatus.IN_PROGRESS) }
    var currentRevision by remember { mutableStateOf(document?.currentRevision ?: "R00") }
    var description by remember { mutableStateOf(document?.description ?: "") }

    // Estados dos dropdowns
    var typeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var disciplineExpanded by remember { mutableStateOf(false) }
    var projectExpanded by remember { mutableStateOf(false) }
    var taskExpanded by remember { mutableStateOf(false) }
    var phaseExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Filtrar tarefas do projeto selecionado
    val filteredTasks = remember(selectedProjectId, availableTasks) {
        if (selectedProjectId != null) {
            availableTasks.filter { it.project.id == selectedProjectId }
        } else {
            emptyList()
        }
    }

    // Resetar task se projeto mudar
    LaunchedEffect(selectedProjectId) {
        if (selectedTaskId != null) {
            val taskBelongsToProject = filteredTasks.any { it.id == selectedTaskId }
            if (!taskBelongsToProject) {
                selectedTaskId = null
            }
        }
    }

    // Validação
    val isValid = title.isNotBlank() &&
            type != null &&
            category != null &&
            discipline != null &&
            selectedProjectId != null &&
            selectedTaskId != null

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (document != null) "Editar Documento" else "Novo Documento",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar"
                        )
                    }
                }

                HorizontalDivider()

                // Formulário
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Código
                    item {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Código do Documento") },
                            placeholder = { Text("Ex: VV-ARQ-001-R00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Título (obrigatório)
                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título *") },
                            placeholder = { Text("Ex: Planta Arquitetônica - Pavimento Tipo") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = title.isBlank(),
                            supportingText = if (title.isBlank()) {
                                { Text("Campo obrigatório") }
                            } else null
                        )
                    }

                    // Tipo de Documento (obrigatório)
                    item {
                        ExposedDropdownMenuBox(
                            expanded = typeExpanded,
                            onExpandedChange = { typeExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = type?.displayName ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo de Documento *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                isError = type == null,
                                supportingText = if (type == null) {
                                    { Text("Campo obrigatório") }
                                } else null
                            )

                            ExposedDropdownMenu(
                                expanded = typeExpanded,
                                onDismissRequest = { typeExpanded = false }
                            ) {
                                DocumentType.entries.forEach { docType ->
                                    DropdownMenuItem(
                                        text = { Text(docType.displayName) },
                                        onClick = {
                                            type = docType
                                            typeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Categoria (obrigatório)
                    item {
                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = category?.displayName ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoria *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                isError = category == null,
                                supportingText = if (category == null) {
                                    { Text("Campo obrigatório") }
                                } else null
                            )

                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false }
                            ) {
                                DocumentCategory.entries.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.displayName) },
                                        onClick = {
                                            category = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Time/Disciplina (obrigatório)
                    item {
                        ExposedDropdownMenuBox(
                            expanded = disciplineExpanded,
                            onExpandedChange = { disciplineExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = discipline?.displayName ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Time/Disciplina *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disciplineExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                isError = discipline == null,
                                supportingText = if (discipline == null) {
                                    { Text("Campo obrigatório") }
                                } else null
                            )

                            ExposedDropdownMenu(
                                expanded = disciplineExpanded,
                                onDismissRequest = { disciplineExpanded = false }
                            ) {
                                DocumentDiscipline.entries.forEach { disc ->
                                    DropdownMenuItem(
                                        text = { Text(disc.displayName) },
                                        onClick = {
                                            discipline = disc
                                            disciplineExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Projeto (obrigatório) - Cascata 1
                    item {
                        ExposedDropdownMenuBox(
                            expanded = projectExpanded,
                            onExpandedChange = { projectExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedProjectId?.let { projId ->
                                    availableProjects.find { it.id == projId }?.name ?: "Selecione um projeto"
                                } ?: "Selecione um projeto",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Projeto/Obra *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = projectExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                isError = selectedProjectId == null,
                                supportingText = if (selectedProjectId == null) {
                                    { Text("Campo obrigatório") }
                                } else null
                            )

                            ExposedDropdownMenu(
                                expanded = projectExpanded,
                                onDismissRequest = { projectExpanded = false }
                            ) {
                                availableProjects.forEach { project ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(project.name, fontWeight = FontWeight.Medium)
                                                Text(
                                                    project.location,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedProjectId = project.id
                                            projectExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Tarefa (obrigatório) - Cascata 2
                    item {
                        ExposedDropdownMenuBox(
                            expanded = taskExpanded,
                            onExpandedChange = {
                                if (selectedProjectId != null && filteredTasks.isNotEmpty()) {
                                    taskExpanded = it
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedTaskId?.let { taskId ->
                                    filteredTasks.find { it.id == taskId }?.title ?: "Selecione uma tarefa"
                                } ?: if (selectedProjectId == null) {
                                    "Primeiro selecione um projeto"
                                } else if (filteredTasks.isEmpty()) {
                                    "Nenhuma tarefa disponível neste projeto"
                                } else {
                                    "Selecione uma tarefa"
                                },
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tarefa *") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = taskExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                enabled = selectedProjectId != null && filteredTasks.isNotEmpty(),
                                isError = selectedTaskId == null,
                                supportingText = if (selectedTaskId == null) {
                                    { Text("Campo obrigatório") }
                                } else null
                            )

                            ExposedDropdownMenu(
                                expanded = taskExpanded,
                                onDismissRequest = { taskExpanded = false }
                            ) {
                                filteredTasks.forEach { task ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(task.title, fontWeight = FontWeight.Medium)
                                                Text(
                                                    "Responsável: ${task.assignedTo.name}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedTaskId = task.id
                                            taskExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Fase
                    item {
                        ExposedDropdownMenuBox(
                            expanded = phaseExpanded,
                            onExpandedChange = { phaseExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = phase.displayName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Fase do Projeto") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = phaseExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = phaseExpanded,
                                onDismissRequest = { phaseExpanded = false }
                            ) {
                                ProjectPhase.entries.forEach { ph ->
                                    DropdownMenuItem(
                                        text = { Text(ph.displayName) },
                                        onClick = {
                                            phase = ph
                                            phaseExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Status
                    item {
                        ExposedDropdownMenuBox(
                            expanded = statusExpanded,
                            onExpandedChange = { statusExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = status.displayName,
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
                                DocumentStatus.entries.forEach { st ->
                                    DropdownMenuItem(
                                        text = { Text(st.displayName) },
                                        onClick = {
                                            status = st
                                            statusExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Revisão
                    item {
                        OutlinedTextField(
                            value = currentRevision,
                            onValueChange = { currentRevision = it },
                            label = { Text("Revisão Atual") },
                            placeholder = { Text("Ex: R00, R01, R02") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    // Descrição
                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Descrição") },
                            placeholder = { Text("Descrição adicional do documento...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }

                HorizontalDivider()

                // Botões
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            val newDocument = Document(
                                id = document?.id ?: "",
                                code = code.trim().ifBlank {
                                    // Gerar código automaticamente se vazio
                                    val projectCode = availableProjects.find { it.id == selectedProjectId }?.name?.take(2)?.uppercase() ?: "XX"
                                    val typeCode = type?.name?.take(3)?.uppercase() ?: "XXX"
                                    "$projectCode-$typeCode-001-$currentRevision"
                                },
                                title = title.trim(),
                                type = type!!,
                                category = category!!,
                                discipline = discipline!!,
                                taskId = selectedTaskId!!,
                                projectId = selectedProjectId!!,
                                phase = phase,
                                status = status,
                                currentRevision = currentRevision.trim(),
                                createdDate = document?.createdDate ?: Clock.System.todayIn(TimeZone.currentSystemDefault()),
                                createdBy = document?.createdBy ?: "1", // TODO: Pegar do usuário logado
                                fileUrl = document?.fileUrl,
                                fileSize = document?.fileSize,
                                tags = document?.tags ?: emptyList(),
                                description = description.trim().ifBlank { null },
                                isSuperseded = document?.isSuperseded ?: false
                            )
                            onSave(newDocument)
                        },
                        enabled = isValid,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (document != null) "Salvar" else "Criar")
                    }
                }
            }
        }
    }
}
