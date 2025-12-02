package com.project.taskmanagercivil.presentation.screens.employees

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeeDetailScreen(
    navController: NavController,
    viewModel: EmployeeDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit = {},
    onDelete: (String) -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onProjectClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("DETALHES") },
                    actions = {
                        IconButton(onClick = { viewModel.refreshEmployee() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Atualizar"
                            )
                        }
                    },
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

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.refreshEmployee() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            uiState.employee != null -> {
                var showGeneralInfoModal by remember { mutableStateOf(false) }

                EmployeeDetailContent(
                    employee = uiState.employee!!,
                    tasks = uiState.tasks,
                    onTaskClick = onTaskClick,
                    onProjectClick = onProjectClick,
                    onEditGeneralInfo = { showGeneralInfoModal = true },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )

                // Modal de edição de informações gerais
                if (showGeneralInfoModal) {
                    EditGeneralInfoModal(
                        employee = uiState.employee!!,
                        onDismiss = { showGeneralInfoModal = false },
                        onSave = { fullName, role, email, phone, cpf ->
                            viewModel.updateEmployeeGeneralInfo(fullName, role, email, phone, cpf)
                            showGeneralInfoModal = false
                        }
                    )
                }
            }
        }

        // Dialog de confirmação de exclusão
        if (showDeleteDialog && uiState.employee != null) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Deletar Colaborador?") },
                text = {
                    Text("Tem certeza que deseja deletar o colaborador \"${uiState.employee!!.fullName}\"? Esta ação não pode ser desfeita.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDelete(uiState.employee!!.id)
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
private fun EmployeeDetailContent(
    employee: Employee,
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    onEditGeneralInfo: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho com Avatar e Nome
        item {
            EmployeeHeader(employee = employee)
        }

        // Informações Gerais
        item {
            GeneralInfoCard(
                employee = employee,
                totalTasks = tasks.size,
                onEdit = onEditGeneralInfo
            )
        }

        // Estatísticas de Tarefas
        item {
            TaskStatsCard(tasks = tasks)
        }

        // Histórico de Tarefas
        item {
            Text(
                text = "Histórico de Tarefas (${tasks.size})",
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
                            text = "Nenhuma tarefa atribuída a este colaborador",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            item {
                TaskHistoryTable(
                    tasks = tasks,
                    onTaskClick = onTaskClick,
                    onProjectClick = onProjectClick
                )
            }
        }
    }
}

@Composable
private fun EmployeeHeader(employee: Employee) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar com iniciais
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = employee.fullName.split(" ")
                    .take(2)
                    .joinToString("") { it.first().uppercase() },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = employee.fullName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = employee.role,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (!employee.isCurrentlyActive()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Inativo",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun GeneralInfoCard(
    employee: Employee,
    totalTasks: Int,
    onEdit: () -> Unit = {}
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
            // Título com botão de 3 pontos
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
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            // Nome Completo
            StampRow(
                label = "Nome:",
                value = employee.fullName
            )

            HorizontalDivider()

            // Cargo
            StampRow(
                label = "Cargo:",
                value = employee.role
            )

            HorizontalDivider()

            // Email
            StampRow(
                label = "Email:",
                value = employee.email
            )

            if (employee.phone != null) {
                HorizontalDivider()
                StampRow(
                    label = "Telefone:",
                    value = employee.phone
                )
            }

            if (employee.cpf != null) {
                HorizontalDivider()
                StampRow(
                    label = "CPF:",
                    value = employee.cpf
                )
            }

            HorizontalDivider()

            // Grid com 3 colunas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Data de Admissão
                StampCell(
                    label = "Data de Admissão",
                    value = FormatUtils.formatDate(employee.hireDate),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Status
                StampCell(
                    label = "Status",
                    value = if (employee.isCurrentlyActive()) "Ativo" else "Inativo",
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Total de Tarefas
                StampCell(
                    label = "Total de Tarefas",
                    value = totalTasks.toString(),
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
            .height(56.dp)
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
            maxLines = 2
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
private fun TaskStatsCard(tasks: List<Task>) {
    val totalTasks = tasks.size
    val todoTasks = tasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.TODO }
    val inProgressTasks = tasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.IN_PROGRESS }
    val inReviewTasks = tasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.IN_REVIEW }
    val completedTasks = tasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED }
    val blockedTasks = tasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.BLOCKED }

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
                text = "Estatísticas de Tarefas",
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

@Composable
private fun TaskHistoryTable(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onProjectClick: (String) -> Unit
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
            // Cabeçalho da Tabela
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
                HeaderCell("Nome da Obra", Modifier.weight(1.2f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Nome da Tarefa", Modifier.weight(1.5f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Descrição", Modifier.weight(2f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Data Início", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Data Conclusão", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Última Revisão", Modifier.weight(0.8f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da Tabela (espaçamento de 4.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tasks.forEach { task ->
                    TaskHistoryRow(
                        task = task,
                        onTaskClick = onTaskClick,
                        onProjectClick = onProjectClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun TaskHistoryRow(
    task: Task,
    onTaskClick: (String) -> Unit,
    onProjectClick: (String) -> Unit
) {
    val lastRevision = task.revisions.maxByOrNull { it.revisionNumber }?.revisionNumber ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .clickable { onTaskClick(task.id) }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nome da Obra (clicável)
        Text(
            text = task.project.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .weight(1.2f)
                .clickable { onProjectClick(task.project.id) }
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        // Nome da Tarefa
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.5f)
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        // Descrição (truncada)
        Text(
            text = task.description.take(50) + if (task.description.length > 50) "..." else "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(2f)
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        // Data de Início
        Text(
            text = FormatUtils.formatDate(task.startDate),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        // Data de Conclusão
        Text(
            text = FormatUtils.formatDate(task.dueDate),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        // Número da Última Revisão
        Text(
            text = lastRevision.toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGeneralInfoModal(
    employee: Employee,
    onDismiss: () -> Unit,
    onSave: (fullName: String, role: String, email: String, phone: String?, cpf: String?) -> Unit
) {
    var fullName by remember { mutableStateOf(employee.fullName) }
    var role by remember { mutableStateOf(employee.role) }
    var email by remember { mutableStateOf(employee.email) }
    var phone by remember { mutableStateOf(employee.phone ?: "") }
    var cpf by remember { mutableStateOf(employee.cpf ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Informações Gerais") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nome Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Cargo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Telefone (opcional)") },
                    placeholder = { Text("(00) 00000-0000") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = cpf,
                    onValueChange = { cpf = it },
                    label = { Text("CPF (opcional)") },
                    placeholder = { Text("000.000.000-00") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        fullName,
                        role,
                        email,
                        phone.ifBlank { null },
                        cpf.ifBlank { null }
                    )
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
