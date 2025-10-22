package com.project.taskmanagercivil.presentation.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.theme.extendedColors
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// Data class para representar histórico de revisões
data class TaskRevision(
    val revisionNumber: Int,
    val author: String,
    val description: String,
    val startDate: LocalDate,
    val deliveryDate: LocalDate
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onProjectClick: (String) -> Unit = {},
    onEmployeeClick: (String) -> Unit = {},
    onRelatedTaskClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var showChecklistDialog by remember { mutableStateOf(false) }
    var selectedDescription by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    if (uiState.task != null) {
                        IconButton(onClick = { onEdit(uiState.task!!.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                    }

                    if (uiState.task != null) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            uiState.task != null -> {
                TaskDetailContent(
                    task = uiState.task!!,
                    onProjectClick = onProjectClick,
                    onEmployeeClick = onEmployeeClick,
                    onDescriptionClick = { description ->
                        selectedDescription = description
                        showDescriptionDialog = true
                    },
                    onPartialDeliveryClick = {
                        showChecklistDialog = true
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }

        // Dialog de confirmação de exclusão
        if (showDeleteDialog && uiState.task != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Deletar Tarefa?") },
                text = {
                    Text("Tem certeza que deseja deletar a tarefa \"${uiState.task!!.title}\"? Esta ação não pode ser desfeita.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete(uiState.task!!.id)
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Deletar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Modal lateral para descrição
        if (showDescriptionDialog) {
            DescriptionDialog(
                description = selectedDescription,
                onDismiss = { showDescriptionDialog = false }
            )
        }

        // Modal de checklist para entrega parcial
        if (showChecklistDialog) {
            ChecklistDialog(
                onDismiss = { showChecklistDialog = false }
            )
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    onProjectClick: (String) -> Unit,
    onEmployeeClick: (String) -> Unit,
    onDescriptionClick: (String) -> Unit,
    onPartialDeliveryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysOverdue = (today.toEpochDays() - task.dueDate.toEpochDays()).toInt()
        .coerceAtLeast(0)
    val daysRemaining = (task.dueDate.toEpochDays() - today.toEpochDays()).toInt()

    // Dados mockados de revisões (futuramente virão do backend)
    val revisions = remember {
        listOf(
            TaskRevision(
                revisionNumber = 3,
                author = task.assignedTo.name,
                description = "Revisão final com ajustes de acordo com as normas ABNT NBR 6118",
                startDate = task.startDate,
                deliveryDate = task.dueDate
            ),
            TaskRevision(
                revisionNumber = 2,
                author = "João Silva",
                description = "Segunda revisão incorporando correções solicitadas pela fiscalização",
                startDate = task.startDate,
                deliveryDate = task.dueDate
            ),
            TaskRevision(
                revisionNumber = 1,
                author = "Maria Santos",
                description = "Primeira revisão do projeto estrutural - análise preliminar",
                startDate = task.startDate,
                deliveryDate = task.dueDate
            ),
            TaskRevision(
                revisionNumber = 0,
                author = task.assignedTo.name,
                description = "Emissão inicial",
                startDate = task.startDate,
                deliveryDate = task.dueDate
            )
        )
    }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Painel 1: Informações Gerais
        item {
            GeneralInfoCard(
                task = task,
                onProjectClick = onProjectClick,
                onEmployeeClick = onEmployeeClick
            )
        }

        // Painel 2: Cronograma
        item {
            ScheduleCard(
                task = task,
                daysOverdue = daysOverdue,
                daysRemaining = daysRemaining
            )
        }

        // Botões de Entrega
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão Entrega Parcial
                Button(
                    onClick = onPartialDeliveryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Entrega Parcial",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Botão Finalizar Tarefa
                Button(
                    onClick = {
                        // TODO: Implementar lógica de finalização quando houver método no repository
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Finalizar Tarefa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Painel 3: Histórico de Revisões
        item {
            RevisionHistoryCard(
                revisions = revisions,
                onDescriptionClick = onDescriptionClick
            )
        }
    }
}

@Composable
private fun GeneralInfoCard(
    task: Task,
    onProjectClick: (String) -> Unit,
    onEmployeeClick: (String) -> Unit
) {
    val colors = MaterialTheme.extendedColors
    val statusColor = when (task.status) {
        TaskStatus.TODO -> colors.statusTodo
        TaskStatus.IN_PROGRESS -> colors.statusInProgress
        TaskStatus.IN_REVIEW -> colors.statusInReview
        TaskStatus.COMPLETED -> colors.statusCompleted
        TaskStatus.BLOCKED -> colors.statusBlocked
    }

    val priorityColor = when (task.priority) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary
        TaskPriority.MEDIUM -> colors.statusInProgress
        TaskPriority.HIGH -> colors.statusInReview
        TaskPriority.CRITICAL -> MaterialTheme.colorScheme.error
    }

    // Número de revisão atual (mockado - futuramente virá do backend)
    val currentRevision = 3

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Informações Gerais",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Linha 1: Nome da Obra
            InfoRow(
                label = "Obra:",
                value = task.project.name,
                isClickable = true,
                onClick = { onProjectClick(task.project.id) }
            )

            HorizontalDivider()

            // Linha 2: Nome da Tarefa
            InfoRow(
                label = "Tarefa:",
                value = task.title
            )

            HorizontalDivider()

            // Linha 3: Responsável
            InfoRow(
                label = "Responsável:",
                value = task.assignedTo.name,
                isClickable = true,
                onClick = { onEmployeeClick(task.assignedTo.id) }
            )

            HorizontalDivider()

            // Linha 4: Endereço Completo
            InfoRow(
                label = "Endereço:",
                value = task.project.location
            )

            HorizontalDivider()

            // Linha 5: Revisão | Disciplina | Status | Prioridade
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Revisão
                InfoColumn(
                    label = "Revisão",
                    value = "Rev. ${currentRevision.toString().padStart(2, '0')}",
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Disciplina
                InfoColumn(
                    label = "Disciplina",
                    value = task.assignedTo.role,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Status
                InfoColumn(
                    label = "Status",
                    value = task.status.label,
                    valueColor = statusColor,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Prioridade
                InfoColumn(
                    label = "Prioridade",
                    value = task.priority.label,
                    valueColor = priorityColor,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    task: Task,
    daysOverdue: Int,
    daysRemaining: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Cronograma",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DateItem(
                    icon = Icons.Default.PlayArrow,
                    label = "Data de Início",
                    value = task.startDate.toString(),
                    iconColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                DateItem(
                    icon = Icons.Default.Flag,
                    label = "Previsão de Término",
                    value = task.dueDate.toString(),
                    iconColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )

                if (daysOverdue > 0) {
                    VerticalDivider(
                        modifier = Modifier
                            .height(56.dp)
                            .padding(horizontal = 8.dp)
                    )

                    DateItem(
                        icon = Icons.Default.Warning,
                        label = "Dias de Atraso",
                        value = "$daysOverdue dias",
                        iconColor = MaterialTheme.colorScheme.error,
                        valueColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun RevisionHistoryCard(
    revisions: List<TaskRevision>,
    onDescriptionClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Histórico de Revisões",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Header da tabela
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeaderCell("Número", Modifier.weight(0.6f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Autor", Modifier.weight(1.2f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Descrição", Modifier.weight(2f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Data Início", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Data Entrega", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da tabela (espaçamento de 4.dp igual a Colaboradores e Tarefas)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                revisions.forEach { revision ->
                    RevisionRow(
                        revision = revision,
                        onDescriptionClick = { onDescriptionClick(revision.description) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RevisionRow(
    revision: TaskRevision,
    onDescriptionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número da revisão
        Text(
            text = "Rev. ${revision.revisionNumber.toString().padStart(2, '0')}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.6f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Autor
        Text(
            text = revision.author,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Descrição (clicável)
        Text(
            text = revision.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(2f)
                .clip(RoundedCornerShape(4.dp))
                .clickable(onClick = onDescriptionClick)
                .padding(4.dp)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Data de Início
        Text(
            text = revision.startDate.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Data de Entrega
        Text(
            text = revision.deliveryDate.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DescriptionDialog(
    description: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Descrição da Revisão",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider()

                // Conteúdo da descrição
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Botão Fechar
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Fechar")
                }
            }
        }
    }
}

@Composable
private fun ChecklistDialog(
    onDismiss: () -> Unit
) {
    // Estado para controlar os checkboxes (mockado - futuramente virá do backend)
    val checklistItems = remember {
        mutableStateListOf(
            ChecklistItem("1. Levantamento topográfico", false),
            ChecklistItem("2. Sondagem do terreno", true),
            ChecklistItem("3. Projeto arquitetônico preliminar", true),
            ChecklistItem("4. Análise de viabilidade técnica", false),
            ChecklistItem("5. Definição de materiais", false),
            ChecklistItem("6. Orçamento detalhado", false),
            ChecklistItem("7. Cronograma executivo", false)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Checklist da Tarefa",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                HorizontalDivider()

                // Checklist
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(checklistItems.size) { index ->
                        val item = checklistItems[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    checklistItems[index] = item.copy(isChecked = !item.isChecked)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = item.isChecked,
                                onCheckedChange = { checked ->
                                    checklistItems[index] = item.copy(isChecked = checked)
                                }
                            )

                            Text(
                                text = item.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Progresso
                val completedCount = checklistItems.count { it.isChecked }
                val totalCount = checklistItems.size
                val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progresso:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$completedCount de $totalCount itens",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Botão Entregar
                Button(
                    onClick = {
                        // TODO: Implementar lógica de entrega parcial
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Entregar")
                }
            }
        }
    }
}

// Data class para itens do checklist
private data class ChecklistItem(
    val text: String,
    val isChecked: Boolean
)

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    isClickable: Boolean = false,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .then(
                if (isClickable) {
                    Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onClick)
                        .padding(4.dp)
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 12.dp)
        )
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isClickable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            if (isClickable) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoColumn(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(48.dp)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun DateItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    iconColor: androidx.compose.ui.graphics.Color,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
        }
    }
}
