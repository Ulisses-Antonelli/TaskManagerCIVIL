package com.project.taskmanagercivil.presentation.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.components.EmployeeTasksTable
import com.project.taskmanagercivil.presentation.components.TaskCard
import com.project.taskmanagercivil.utils.calculateDerivedStatus
import com.project.taskmanagercivil.utils.calculateProgress
import com.project.taskmanagercivil.utils.formatCurrency
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onEmployeeClick: (String) -> Unit = {},
    onTeamClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do Projeto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    
                    if (uiState.project != null) {
                        IconButton(onClick = { onEdit(uiState.project!!.id) }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar"
                            )
                        }
                    }

                    
                    if (uiState.project != null) {
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

            uiState.project != null -> {
                ProjectDetailContent(
                    project = uiState.project!!,
                    tasks = uiState.tasks,
                    onTaskClick = onTaskClick,
                    onEmployeeClick = onEmployeeClick,
                    onTeamClick = onTeamClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }

        // Dialog de confirmação de exclusão
        if (showDeleteDialog && uiState.project != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Deletar Projeto?") },
                text = {
                    Text("Tem certeza que deseja deletar o projeto \"${uiState.project!!.name}\"? Esta ação não pode ser desfeita.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete(uiState.project!!.id)
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
    }
}

@Composable
private fun ProjectDetailContent(
    project: com.project.taskmanagercivil.domain.models.Project,
    tasks: List<com.project.taskmanagercivil.domain.models.Task>,
    onTaskClick: (String) -> Unit,
    onEmployeeClick: (String) -> Unit,
    onTeamClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val derivedStatus = project.calculateDerivedStatus(tasks)
    val progress = project.calculateProgress(tasks)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysOverdue = (today.toEpochDays() - project.endDate.toEpochDays()).toInt()
        .coerceAtLeast(0)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho do Projeto
        item {
            ProjectHeader(project = project)
        }

        // Informações Gerais
        item {
            GeneralInfoCard(
                project = project,
                derivedStatus = derivedStatus,
                progress = progress,
                daysOverdue = daysOverdue,
                totalTasks = tasks.size
            )
        }

        // Estatísticas do Projeto
        item {
            ProjectStatsCard(tasks = tasks)
        }

        // Datas
        item {
            DatesCard(project = project, daysOverdue = daysOverdue)
        }

        // Tabela de Colaboradores e Tarefas
        if (tasks.isNotEmpty()) {
            item {
                EmployeeTasksTable(
                    tasks = tasks,
                    onEmployeeClick = onEmployeeClick,
                    onTeamClick = onTeamClick
                )
            }
        }

        // Lista de Tarefas
        item {
            Text(
                text = "Tarefas do Projeto (${tasks.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (tasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma tarefa vinculada a este projeto",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(tasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    onClick = { onTaskClick(task.id) }
                )
            }
        }
    }
}

@Composable
private fun ProjectHeader(project: com.project.taskmanagercivil.domain.models.Project) {
    Column {
        Text(
            text = project.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = project.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GeneralInfoCard(
    project: com.project.taskmanagercivil.domain.models.Project,
    derivedStatus: TaskStatus,
    progress: Float,
    daysOverdue: Int,
    totalTasks: Int
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
                text = "Informações Gerais",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            InfoRow(
                icon = Icons.Default.Person,
                label = "Cliente/Executor",
                value = project.client
            )

            InfoRow(
                icon = Icons.Default.Build,
                label = "Tipo de Projeto",
                value = project.description.take(50) // Placeholder - em produção seria um campo específico
            )

            InfoRow(
                icon = Icons.Default.LocationOn,
                label = "Localização",
                value = project.location
            )

            InfoRow(
                icon = Icons.Default.AccountBalance,
                label = "Orçamento Estimado",
                value = formatCurrency(project.budget)
            )

            InfoRow(
                icon = Icons.Default.Assessment,
                label = "Progresso Geral",
                value = "${progress.toInt()}%"
            )

            InfoRow(
                icon = Icons.Default.Assignment,
                label = "Status Derivado",
                value = derivedStatus.label
            )

            InfoRow(
                icon = Icons.Default.Task,
                label = "Total de Tarefas",
                value = totalTasks.toString()
            )

            if (daysOverdue > 0) {
                InfoRow(
                    icon = Icons.Default.Warning,
                    label = "Dias de Atraso",
                    value = "$daysOverdue dias"
                )
            }
        }
    }
}

@Composable
private fun ProjectStatsCard(tasks: List<com.project.taskmanagercivil.domain.models.Task>) {
    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
    val inProgressTasks = tasks.count { it.status == TaskStatus.IN_PROGRESS }
    val blockedTasks = tasks.count { it.status == TaskStatus.BLOCKED }
    val criticalTasks = tasks.count { it.priority == TaskPriority.CRITICAL }

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
                text = "Estatísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Total", value = totalTasks.toString())
                StatItem(label = "Concluídas", value = completedTasks.toString())
                StatItem(label = "Em Andamento", value = inProgressTasks.toString())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Bloqueadas", value = blockedTasks.toString())
                StatItem(label = "Críticas", value = criticalTasks.toString())
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DatesCard(
    project: com.project.taskmanagercivil.domain.models.Project,
    daysOverdue: Int
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            InfoRow(
                icon = Icons.Default.PlayArrow,
                label = "Data de Início",
                value = project.startDate.toString()
            )

            InfoRow(
                icon = Icons.Default.Flag,
                label = "Previsão de Término",
                value = project.endDate.toString()
            )

            if (daysOverdue > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dias de Atraso",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "$daysOverdue dias",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
