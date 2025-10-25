package com.project.taskmanagercivil.presentation.screens.projects

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.EmployeeTasksTable
import com.project.taskmanagercivil.presentation.components.TaskCard
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.utils.calculateDerivedStatus
import com.project.taskmanagercivil.utils.calculateProgress
import com.project.taskmanagercivil.utils.formatCurrency
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    navController: NavController,
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
            Column {
                TopAppBar(
                    title = { Text("DETALHES") },
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
    Text(
        text = project.name,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
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
            // Título alinhado à esquerda
            Text(
                text = "Informações Gerais",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Linha 1: Obra
            StampRow(
                label = "Obra:",
                value = project.name
            )

            HorizontalDivider()

            // Linha 2: Tipo
            StampRow(
                label = "Tipo:",
                value = project.description.take(50)
            )

            HorizontalDivider()

            // Linha 3: Endereço
            StampRow(
                label = "Endereço:",
                value = project.location
            )

            HorizontalDivider()

            // Linha 4: Grid com 2 colunas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Coluna 1: Orçamento
                StampCell(
                    label = "Orçamento",
                    value = formatCurrency(project.budget),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Coluna 2: Progresso geral
                StampCell(
                    label = "Progresso geral",
                    value = "${progress.toInt()}%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StampRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
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
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StampCell(
    label: String,
    value: String,
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
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun ProjectStatsCard(tasks: List<com.project.taskmanagercivil.domain.models.Task>) {
    val totalTasks = tasks.size
    val todoTasks = tasks.count { it.status == TaskStatus.TODO }
    val inProgressTasks = tasks.count { it.status == TaskStatus.IN_PROGRESS }
    val inReviewTasks = tasks.count { it.status == TaskStatus.IN_REVIEW }
    val completedTasks = tasks.count { it.status == TaskStatus.COMPLETED }
    val blockedTasks = tasks.count { it.status == TaskStatus.BLOCKED }

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
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Total", value = totalTasks.toString(), modifier = Modifier.weight(1f))

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                StatItem(label = "A Fazer", value = todoTasks.toString(), modifier = Modifier.weight(1f))

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                StatItem(label = "Em Andamento", value = inProgressTasks.toString(), modifier = Modifier.weight(1f))

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                StatItem(label = "Em Revisão", value = inReviewTasks.toString(), modifier = Modifier.weight(1f))

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                StatItem(label = "Concluídas", value = completedTasks.toString(), modifier = Modifier.weight(1f))

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                StatItem(label = "Bloqueadas", value = blockedTasks.toString(), modifier = Modifier.weight(1f))
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
                    value = project.startDate.toString(),
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
                    value = project.endDate.toString(),
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

@Composable
private fun StatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
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
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}
