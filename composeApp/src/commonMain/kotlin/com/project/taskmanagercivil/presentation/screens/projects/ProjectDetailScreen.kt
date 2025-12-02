package com.project.taskmanagercivil.presentation.screens.projects

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.datetime.*

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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
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
                    availableEmployees = uiState.availableEmployees,
                    availableTeams = uiState.availableTeams,
                    onEdit = onEdit,
                    onTaskClick = onTaskClick,
                    onEmployeeClick = onEmployeeClick,
                    onTeamClick = onTeamClick,
                    onUpdateTaskAssignment = { taskId, employeeId, teamId ->
                        viewModel.updateTaskAssignment(taskId, employeeId, teamId)
                    },
                    onUpdateGeneralInfo = { name, description, client, location, budget ->
                        viewModel.updateProjectGeneralInfo(name, description, client, location, budget)
                    },
                    onUpdateSchedule = { startDate, endDate ->
                        viewModel.updateProjectSchedule(startDate, endDate)
                    },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGeneralInfoModal(
    project: com.project.taskmanagercivil.domain.models.Project,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, client: String, location: String, budget: Double) -> Unit
) {
    var name by remember { mutableStateOf(project.name) }
    var description by remember { mutableStateOf(project.description) }
    var client by remember { mutableStateOf(project.client) }
    var location by remember { mutableStateOf(project.location) }
    var budget by remember { mutableStateOf(project.budget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Informações Gerais") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome da Obra") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                OutlinedTextField(
                    value = client,
                    onValueChange = { client = it },
                    label = { Text("Cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Localização") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Orçamento (R$)") },
                    placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val budgetValue = budget.toDoubleOrNull() ?: 0.0
                        onSave(name, description, client, location, budgetValue)
                        onDismiss()
                    } catch (e: Exception) {
                        // TODO: Mostrar erro de validação
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditScheduleModal(
    project: com.project.taskmanagercivil.domain.models.Project,
    onDismiss: () -> Unit,
    onSave: (LocalDate, LocalDate) -> Unit
) {
    var startDate by remember { mutableStateOf(project.startDate.toString()) }
    var endDate by remember { mutableStateOf(project.endDate.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Cronograma") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Data de Início") },
                    placeholder = { Text("AAAA-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("Previsão de Término") },
                    placeholder = { Text("AAAA-MM-DD") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text(
                    text = "Formato: AAAA-MM-DD (ex: 2024-12-31)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val start = LocalDate.parse(startDate)
                        val end = LocalDate.parse(endDate)
                        onSave(start, end)
                        onDismiss()
                    } catch (e: Exception) {
                        // TODO: Mostrar erro de validação
                    }
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTaskAssignmentModal(
    task: com.project.taskmanagercivil.domain.models.Task,
    availableEmployees: List<com.project.taskmanagercivil.domain.models.Employee>,
    availableTeams: List<com.project.taskmanagercivil.domain.models.Team>,
    onDismiss: () -> Unit,
    onSave: (employeeId: String, teamId: String) -> Unit
) {
    var selectedEmployeeId by remember { mutableStateOf(task.assignedTo.id) }
    var selectedTeamId by remember { mutableStateOf(task.assignedTo.id) } // TODO: Obter teamId real

    var employeeSearchQuery by remember { mutableStateOf("") }
    var teamSearchQuery by remember { mutableStateOf("") }

    var showEmployeeDropdown by remember { mutableStateOf(false) }
    var showTeamDropdown by remember { mutableStateOf(false) }

    // Filtra equipes ativas
    val filteredTeams = availableTeams.filter {
        it.isActive && it.name.contains(teamSearchQuery, ignoreCase = true)
    }

    // Filtra colaboradores ativos E que pertencem à equipe selecionada (se houver)
    val filteredEmployees = availableEmployees.filter { employee ->
        val matchesTeam = if (selectedTeamId.isNotEmpty()) {
            // Filtra por equipe selecionada - verifica se o colaborador está na equipe
            val team = availableTeams.find { it.id == selectedTeamId }
            team?.memberIds?.contains(employee.id) == true
        } else {
            true // Se não há equipe selecionada, mostra todos
        }

        employee.isActive &&
        matchesTeam &&
        employee.fullName.contains(employeeSearchQuery, ignoreCase = true)
    }

    val selectedEmployee = availableEmployees.find { it.id == selectedEmployeeId }
    val selectedTeam = availableTeams.find { it.id == selectedTeamId }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Atribuição da Tarefa") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Tarefa: ${task.title}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                // Equipe/Disciplina (PRIMEIRO)
                Text(
                    text = "Equipe/Disciplina",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = showTeamDropdown,
                    onExpandedChange = { showTeamDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedTeam?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Selecione a equipe") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTeamDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = showTeamDropdown,
                        onDismissRequest = { showTeamDropdown = false }
                    ) {
                        // Campo de busca
                        OutlinedTextField(
                            value = teamSearchQuery,
                            onValueChange = { teamSearchQuery = it },
                            placeholder = { Text("Buscar equipe...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            singleLine = true
                        )

                        HorizontalDivider()

                        filteredTeams.forEach { team ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(team.name)
                                        Text(
                                            text = team.department.displayName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTeamId = team.id
                                    showTeamDropdown = false
                                    teamSearchQuery = ""
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Colaborador (SEGUNDO - filtrado pela equipe selecionada)
                Text(
                    text = "Colaborador",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                ExposedDropdownMenuBox(
                    expanded = showEmployeeDropdown,
                    onExpandedChange = { showEmployeeDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedEmployee?.fullName ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Selecione o colaborador") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showEmployeeDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = showEmployeeDropdown,
                        onDismissRequest = { showEmployeeDropdown = false }
                    ) {
                        // Campo de busca
                        OutlinedTextField(
                            value = employeeSearchQuery,
                            onValueChange = { employeeSearchQuery = it },
                            placeholder = { Text("Buscar colaborador...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            singleLine = true
                        )

                        HorizontalDivider()

                        filteredEmployees.forEach { employee ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(employee.fullName)
                                        Text(
                                            text = employee.role,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedEmployeeId = employee.id
                                    showEmployeeDropdown = false
                                    employeeSearchQuery = ""
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(selectedEmployeeId, selectedTeamId)
                    onDismiss()
                }
            ) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ProjectDetailContent(
    project: com.project.taskmanagercivil.domain.models.Project,
    tasks: List<com.project.taskmanagercivil.domain.models.Task>,
    availableEmployees: List<com.project.taskmanagercivil.domain.models.Employee>,
    availableTeams: List<com.project.taskmanagercivil.domain.models.Team>,
    onEdit: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onEmployeeClick: (String) -> Unit,
    onTeamClick: (String) -> Unit,
    onUpdateTaskAssignment: (String, String, String) -> Unit,
    onUpdateGeneralInfo: (String, String, String, String, Double) -> Unit,
    onUpdateSchedule: (LocalDate, LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val derivedStatus = project.calculateDerivedStatus(tasks)
    val progress = project.calculateProgress(tasks)
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysOverdue = (today.toEpochDays() - project.endDate.toEpochDays()).toInt()
        .coerceAtLeast(0)

    var showGeneralInfoModal by remember { mutableStateOf(false) }
    var showScheduleModal by remember { mutableStateOf(false) }
    var showAssignmentModal by remember { mutableStateOf(false) }
    var selectedTaskForEdit by remember { mutableStateOf<com.project.taskmanagercivil.domain.models.Task?>(null) }
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
                totalTasks = tasks.size,
                onEdit = { showGeneralInfoModal = true }
            )
        }

        // Estatísticas do Projeto
        item {
            ProjectStatsCard(tasks = tasks)
        }

        // Cronograma
        item {
            DatesCard(
                project = project,
                daysOverdue = daysOverdue,
                onEdit = { showScheduleModal = true }
            )
        }

        // Tabela de Colaboradores e Tarefas
        if (tasks.isNotEmpty()) {
            item {
                EmployeeTasksTable(
                    tasks = tasks,
                    availableTeams = availableTeams,
                    onEmployeeClick = onEmployeeClick,
                    onTeamClick = onTeamClick,
                    onEditTaskAssignment = { taskId, _, _ ->
                        selectedTaskForEdit = tasks.find { it.id == taskId }
                        showAssignmentModal = true
                    }
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

    // Modal de edição de informações gerais
    if (showGeneralInfoModal) {
        EditGeneralInfoModal(
            project = project,
            onDismiss = { showGeneralInfoModal = false },
            onSave = { name, description, client, location, budget ->
                onUpdateGeneralInfo(name, description, client, location, budget)
                showGeneralInfoModal = false
            }
        )
    }

    // Modal de edição de cronograma
    if (showScheduleModal) {
        EditScheduleModal(
            project = project,
            onDismiss = { showScheduleModal = false },
            onSave = { startDate, endDate ->
                onUpdateSchedule(startDate, endDate)
                showScheduleModal = false
            }
        )
    }

    // Modal de edição de atribuição de tarefa
    if (showAssignmentModal && selectedTaskForEdit != null) {
        EditTaskAssignmentModal(
            task = selectedTaskForEdit!!,
            availableEmployees = availableEmployees,
            availableTeams = availableTeams,
            onDismiss = {
                showAssignmentModal = false
                selectedTaskForEdit = null
            },
            onSave = { employeeId, teamId ->
                onUpdateTaskAssignment(selectedTaskForEdit!!.id, employeeId, teamId)
            }
        )
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
    totalTasks: Int,
    onEdit: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
            // Título com ícone de menu
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Informações Gerais",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(40.dp)) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções",
                            modifier = Modifier.size(24.dp)
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
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                    }
                }
            }

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
    daysOverdue: Int,
    onEdit: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

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
            // Título com ícone de menu
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cronograma",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Box(modifier = Modifier.size(40.dp)) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Editar Datas") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                    }
                }
            }

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
