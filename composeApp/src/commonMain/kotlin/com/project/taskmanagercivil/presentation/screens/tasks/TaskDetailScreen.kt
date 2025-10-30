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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.ChecklistItem
import com.project.taskmanagercivil.domain.models.PartialDelivery
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskRevision
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.presentation.theme.extendedColors
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// Data class para armazenar dados pendentes de entrega parcial
private data class PartialDeliveryData(
    val description: String,
    val completedItems: Int,
    val totalItems: Int,
    val checklistItems: List<ChecklistItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    viewModel: TaskDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onProjectClick: (String) -> Unit = {},
    onEmployeeClick: (String) -> Unit = {},
    onRelatedTaskClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDescriptionDialog by remember { mutableStateOf(false) }
    var showChecklistDialog by remember { mutableStateOf(false) }
    var showDeliveryDialog by remember { mutableStateOf(false) }
    var showRevisionDialog by remember { mutableStateOf(false) }
    var showDeliveryDialogAfterChecklist by remember { mutableStateOf(false) }
    var selectedDescription by remember { mutableStateOf("") }
    var selectedIsEdited by remember { mutableStateOf(false) }
    var selectedRevisionNumber by remember { mutableStateOf<Int?>(null) }
    var selectedDeliveryNumber by remember { mutableStateOf<Int?>(null) }
    var pendingPartialDeliveryData by remember { mutableStateOf<PartialDeliveryData?>(null) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("DETALHES") },
                    actions = {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Atualizar"
                            )
                        }
                    }
                )
                DynamicBreadcrumbs(
                    navController = navController,
                    currentRoot = NavigationState.currentRoot
                )
            }
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
                    onRevisionDescriptionClick = { revision ->
                        selectedDescription = revision.description
                        selectedIsEdited = revision.isEdited
                        selectedRevisionNumber = revision.revisionNumber
                        selectedDeliveryNumber = null
                        showDescriptionDialog = true
                    },
                    onDeliveryDescriptionClick = { delivery ->
                        selectedDescription = delivery.description
                        selectedIsEdited = delivery.isEdited
                        selectedRevisionNumber = null
                        selectedDeliveryNumber = delivery.deliveryNumber
                        showDescriptionDialog = true
                    },
                    onPartialDeliveryClick = {
                        showChecklistDialog = true
                    },
                    onDeliverTaskClick = {
                        showDeliveryDialog = true
                    },
                    onRequestRevisionClick = {
                        showRevisionDialog = true
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }

        // Modal lateral para descrição
        if (showDescriptionDialog) {
            DescriptionDialog(
                description = selectedDescription,
                isEdited = selectedIsEdited,
                onDismiss = {
                    showDescriptionDialog = false
                    selectedRevisionNumber = null
                    selectedDeliveryNumber = null
                },
                onSave = { newDescription ->
                    when {
                        selectedRevisionNumber != null -> {
                            viewModel.updateRevisionDescription(selectedRevisionNumber!!, newDescription)
                        }
                        selectedDeliveryNumber != null -> {
                            viewModel.updatePartialDeliveryDescription(selectedDeliveryNumber!!, newDescription)
                        }
                    }
                    showDescriptionDialog = false
                    selectedRevisionNumber = null
                    selectedDeliveryNumber = null
                }
            )
        }

        // Modal de checklist para entrega parcial
        if (showChecklistDialog && uiState.task != null) {
            ChecklistDialog(
                task = uiState.task!!,
                onDismiss = { showChecklistDialog = false },
                onConfirm = { description, completedItems, totalItems, checklistItems ->
                    if (completedItems >= totalItems) {
                        // Todos os itens completos - guardar dados e abrir diálogo de entrega
                        pendingPartialDeliveryData = PartialDeliveryData(description, completedItems, totalItems, checklistItems)
                        showChecklistDialog = false
                        showDeliveryDialogAfterChecklist = true
                    } else {
                        // Entrega parcial normal
                        viewModel.createPartialDelivery(description, completedItems, totalItems, checklistItems)
                        showChecklistDialog = false
                    }
                }
            )
        }

        // Dialog de confirmação de entrega
        if (showDeliveryDialog && uiState.task != null) {
            val task = uiState.task!!
            val completedItems = task.checklistItems.count { it.isCompleted }
            val totalItems = task.checklistItems.size
            val hasIncompleteItems = totalItems > 0 && completedItems < totalItems

            DeliveryConfirmationDialog(
                onDismiss = { showDeliveryDialog = false },
                onConfirm = { description ->
                    viewModel.deliverTask(description)
                    showDeliveryDialog = false
                },
                hasIncompleteItems = hasIncompleteItems,
                incompleteCount = totalItems - completedItems,
                totalCount = totalItems
            )
        }

        // Dialog de solicitação de revisão
        if (showRevisionDialog) {
            RevisionReasonDialog(
                onDismiss = { showRevisionDialog = false },
                onConfirm = { checklistItems ->
                    viewModel.requestRevision(checklistItems)
                    showRevisionDialog = false
                }
            )
        }

        // Dialog de confirmação de entrega após checklist completo
        if (showDeliveryDialogAfterChecklist && pendingPartialDeliveryData != null) {
            DeliveryConfirmationDialog(
                onDismiss = {
                    showDeliveryDialogAfterChecklist = false
                    pendingPartialDeliveryData = null
                },
                onConfirm = { revisionDescription ->
                    val data = pendingPartialDeliveryData!!
                    viewModel.createPartialDelivery(
                        description = data.description,
                        completedItems = data.completedItems,
                        totalItems = data.totalItems,
                        checklistItems = data.checklistItems,
                        revisionDescription = revisionDescription
                    )
                    showDeliveryDialogAfterChecklist = false
                    pendingPartialDeliveryData = null
                },
                hasIncompleteItems = false,
                incompleteCount = 0,
                totalCount = 0
            )
        }
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    onProjectClick: (String) -> Unit,
    onEmployeeClick: (String) -> Unit,
    onRevisionDescriptionClick: (TaskRevision) -> Unit,
    onDeliveryDescriptionClick: (PartialDelivery) -> Unit,
    onPartialDeliveryClick: () -> Unit,
    onDeliverTaskClick: () -> Unit,
    onRequestRevisionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysOverdue = (today.toEpochDays() - task.dueDate.toEpochDays()).toInt()
        .coerceAtLeast(0)
    val daysRemaining = (task.dueDate.toEpochDays() - today.toEpochDays()).toInt()

    // Usa as revisões da tarefa (se vazia, mostra mensagem)
    val revisions = task.revisions

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

        // Botões de Ação
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão Entregar Tarefa - visível quando status != COMPLETED
                if (task.status != TaskStatus.COMPLETED) {
                    Button(
                        onClick = onDeliverTaskClick,
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
                            text = "Entregar Tarefa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botão Solicitar Revisão - visível quando status == COMPLETED
                if (task.status == TaskStatus.COMPLETED) {
                    Button(
                        onClick = onRequestRevisionClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RateReview,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Solicitar Revisão",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botão Entrega Parcial - sempre visível
                Button(
                    onClick = onPartialDeliveryClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
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
            }
        }

        // Painel 3: Histórico de Revisões
        item {
            RevisionHistoryCard(
                revisions = revisions,
                onDescriptionClick = onRevisionDescriptionClick
            )
        }

        // Painel 4: Entregas Parciais
        item {
            PartialDeliveriesCard(
                partialDeliveries = task.partialDeliveries,
                onDescriptionClick = onDeliveryDescriptionClick
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
        TaskStatus.INATIVA -> Color(0xFF757575) // Cinza
    }

    val priorityColor = when (task.priority) {
        TaskPriority.LOW -> MaterialTheme.colorScheme.primary
        TaskPriority.MEDIUM -> colors.statusInProgress
        TaskPriority.HIGH -> colors.statusInReview
        TaskPriority.CRITICAL -> MaterialTheme.colorScheme.error
    }

    // Número de revisão atual (maior número de revisão)
    val currentRevision = task.revisions.maxOfOrNull { it.revisionNumber } ?: 0

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

                // Papel
                InfoColumn(
                    label = "Papel",
                    value = task.assignedTo.primaryRoleDisplayName,
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
    onDescriptionClick: (TaskRevision) -> Unit
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
                        onDescriptionClick = { onDescriptionClick(revision) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PartialDeliveriesCard(
    partialDeliveries: List<PartialDelivery>,
    onDescriptionClick: (PartialDelivery) -> Unit
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
                text = "Entregas Parciais",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (partialDeliveries.isEmpty()) {
                Text(
                    text = "Nenhuma entrega parcial registrada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
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
                    HeaderCell("Entrega", Modifier.weight(0.6f))
                    VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                    HeaderCell("Autor", Modifier.weight(1.2f))
                    VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                    HeaderCell("Descrição", Modifier.weight(2f))
                    VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                    HeaderCell("Data Entrega", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                    HeaderCell("Progresso", Modifier.weight(0.8f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Linhas da tabela
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    partialDeliveries.forEach { delivery ->
                        PartialDeliveryRow(
                            delivery = delivery,
                            onDescriptionClick = { onDescriptionClick(delivery) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PartialDeliveryRow(
    delivery: PartialDelivery,
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
        // Número da entrega
        Text(
            text = "Entrega ${delivery.deliveryNumber.toString().padStart(2, '0')}",
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
            text = delivery.author,
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

        // Descrição (clicável) com ícone se editada
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (delivery.isEdited) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Editado",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = delivery.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onDescriptionClick)
                    .padding(4.dp)
            )
        }

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Data de Entrega
        Text(
            text = delivery.deliveryDate.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .padding(horizontal = 4.dp)
        )

        // Progresso
        Text(
            text = "${delivery.completedItems}/${delivery.totalItems}",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(0.8f)
        )
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

        // Descrição (clicável) com ícone se editada
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (revision.isEdited) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Editado",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = revision.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onDescriptionClick)
                    .padding(4.dp)
            )
        }

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
    isEdited: Boolean = false,
    onDismiss: () -> Unit,
    onSave: ((String) -> Unit)? = null
) {
    var editedDescription by remember { mutableStateOf(description) }
    var isEditMode by remember { mutableStateOf(false) }

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
                        text = "Descrição",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (onSave != null && !isEditMode) {
                            IconButton(onClick = { isEditMode = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Fechar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                HorizontalDivider()

                // Badge de aviso se foi editado
                if (isEdited) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Esta descrição foi editada",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // Conteúdo da descrição
                if (isEditMode) {
                    OutlinedTextField(
                        value = editedDescription,
                        onValueChange = { editedDescription = it },
                        label = { Text("Editar Descrição") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        minLines = 8,
                        maxLines = 20,
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
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
                }

                // Botões
                if (isEditMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                editedDescription = description
                                isEditMode = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                onSave?.invoke(editedDescription)
                                isEditMode = false
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = editedDescription.isNotBlank() && editedDescription != description
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Salvar")
                        }
                    }
                } else {
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
}

@Composable
private fun ChecklistDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirm: (description: String, completedItems: Int, totalItems: Int, checklistItems: List<ChecklistItem>) -> Unit
) {
    // Estado para controlar os checkboxes - carrega da tarefa real
    val checklistItems = remember {
        mutableStateListOf<ChecklistItem>().apply {
            if (task.checklistItems.isNotEmpty()) {
                addAll(task.checklistItems)
            } else {
                // Se não houver checklist, adiciona itens padrão
                add(ChecklistItem("1. Levantamento topográfico", false))
                add(ChecklistItem("2. Sondagem do terreno", false))
                add(ChecklistItem("3. Projeto arquitetônico preliminar", false))
                add(ChecklistItem("4. Análise de viabilidade técnica", false))
                add(ChecklistItem("5. Definição de materiais", false))
                add(ChecklistItem("6. Orçamento detalhado", false))
                add(ChecklistItem("7. Cronograma executivo", false))
            }
        }
    }

    var description by remember { mutableStateOf("") }

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
                                    checklistItems[index] = item.copy(isCompleted = !item.isCompleted)
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = item.isCompleted,
                                onCheckedChange = { checked ->
                                    checklistItems[index] = item.copy(isCompleted = checked)
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

                // Campo de Descrição
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição da entrega parcial *") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp),
                    placeholder = { Text("Descreva o que foi entregue nesta etapa...") }
                )

                // Progresso
                val completedCount = checklistItems.count { it.isCompleted }
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

                // Botões
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                            if (description.isNotBlank()) {
                                onConfirm(description, completedCount, totalCount, checklistItems.toList())
                            }
                        },
                        enabled = description.isNotBlank(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (completedCount >= totalCount) "Entregar Tarefa" else "Confirmar Entrega")
                    }
                }
            }
        }
    }
}

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
