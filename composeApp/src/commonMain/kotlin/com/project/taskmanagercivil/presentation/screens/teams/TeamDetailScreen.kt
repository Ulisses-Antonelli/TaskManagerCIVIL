package com.project.taskmanagercivil.presentation.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Project
import com.project.taskmanagercivil.domain.models.Task
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(
    navController: NavController,
    viewModel: TeamDetailViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEmployeeClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    onTaskClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("DETALHES") },
                    actions = {
                        IconButton(onClick = { viewModel.refreshTeam() }) {
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refreshTeam() }) {
                            Text("Tentar novamente")
                        }
                    }
                }

                uiState.team != null -> {
                    var showGeneralInfoModal by remember { mutableStateOf(false) }

                    TeamDetailContent(
                        team = uiState.team!!,
                        leader = uiState.leader,
                        members = uiState.members,
                        projects = uiState.projects,
                        tasks = uiState.tasks,
                        onEmployeeClick = onEmployeeClick,
                        onProjectClick = onProjectClick,
                        onTaskClick = onTaskClick,
                        onEditGeneralInfo = { showGeneralInfoModal = true }
                    )

                    // Modal de edição de informações gerais
                    if (showGeneralInfoModal) {
                        EditGeneralInfoModal(
                            team = uiState.team!!,
                            availableEmployees = uiState.members, // Passando membros atuais como base
                            allEmployees = uiState.members, // TODO: Buscar todos colaboradores ativos
                            onDismiss = { showGeneralInfoModal = false },
                            onSave = { description, leaderId, memberIds ->
                                viewModel.updateTeamGeneralInfo(description, leaderId, memberIds)
                                showGeneralInfoModal = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamDetailContent(
    team: Team,
    leader: Employee?,
    members: List<Employee>,
    projects: List<Project>,
    tasks: List<Task>,
    onEmployeeClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onEditGeneralInfo: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabeçalho com Avatar e Nome
        item {
            TeamHeader(team = team, leader = leader)
        }

        // Informações Gerais
        item {
            GeneralInfoCard(
                team = team,
                membersCount = members.size,
                leader = leader,
                tasks = tasks,
                onEdit = onEditGeneralInfo
            )
        }

        // Quadro de Membros do Time
        item {
            Text(
                text = "Membros do time de: ${team.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        if (members.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum membro atribuído",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            item {
                TeamMembersTable(
                    members = members,
                    leaderId = leader?.id,
                    tasks = tasks,
                    onEmployeeClick = onEmployeeClick
                )
            }
        }

        // Histórico de Tarefas do Time
        item {
            Text(
                text = "Histórico de Tarefas do Time de ${team.name} (${tasks.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (tasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma tarefa atribuída aos membros deste time",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            item {
                TeamTasksTable(
                    tasks = tasks,
                    onTaskClick = onTaskClick,
                    onProjectClick = onProjectClick
                )
            }
        }
    }
}

// Cabeçalho com Avatar do Time
@Composable
private fun TeamHeader(team: Team, leader: Employee?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar com iniciais do departamento
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(getDepartmentColor(team.department)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getDepartmentInitials(team.department),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = team.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!team.isActive) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Inativo",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (leader != null) {
                Text(
                    text = "Líder: ${leader.fullName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// Card de Informações Gerais
@Composable
private fun GeneralInfoCard(
    team: Team,
    membersCount: Int,
    leader: Employee?,
    tasks: List<Task>,
    onEdit: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    // Calcular progresso do time como média ponderada das tarefas
    val teamProgress = if (tasks.isNotEmpty()) {
        tasks.sumOf { it.progress.toDouble() }.toInt() / tasks.size
    } else {
        0
    }

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

            // Escopo de Trabalho / Descrição
            if (team.description.isNotEmpty()) {
                StampRow(
                    label = "Escopo de Trabalho da Equipe:",
                    value = team.description
                )
                HorizontalDivider()
            }

            // Líder
            if (leader != null) {
                StampRow(
                    label = "Líder:",
                    value = leader.fullName
                )
                HorizontalDivider()
            }

            // Grid com 4 colunas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Quantidade de Membros
                StampCell(
                    label = "Quantidade de Membros",
                    value = membersCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Data de Criação
                StampCell(
                    label = "Data de Criação",
                    value = FormatUtils.formatDate(team.createdDate),
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
                    value = if (team.isActive) "Ativo" else "Inativo",
                    modifier = Modifier.weight(1f)
                )

                VerticalDivider(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(horizontal = 8.dp)
                )

                // Progresso
                StampCell(
                    label = "Progresso",
                    value = "$teamProgress%",
                    valueColor = when {
                        teamProgress >= 75 -> Color(0xFF4CAF50) // Verde
                        teamProgress >= 50 -> Color(0xFFFFC107) // Amarelo
                        teamProgress >= 25 -> Color(0xFFFF9800) // Laranja
                        else -> Color(0xFFF44336) // Vermelho
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // Nota explicativa sobre o progresso
            Text(
                text = "* O progresso do time é calculado como média ponderada das tarefas atribuídas aos membros.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )
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
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StampCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color? = null
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
            color = valueColor ?: MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

// Tabela de Membros do Time
@Composable
private fun TeamMembersTable(
    members: List<Employee>,
    leaderId: String?,
    tasks: List<Task>,
    onEmployeeClick: (String) -> Unit
) {
    // Calcular desempenho do time (% de tarefas concluídas)
    val completedTasks = tasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED }
    val teamPerformance = if (tasks.isNotEmpty()) (completedTasks.toFloat() / tasks.size * 100).toInt() else 0

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
                HeaderCell("Nome do Colaborador", Modifier.weight(2f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Função", Modifier.weight(1.5f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Liderança", Modifier.weight(0.8f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Tarefas", Modifier.weight(0.6f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Desempenho", Modifier.weight(0.8f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da Tabela
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                members.forEach { member ->
                    val memberTasks = tasks.filter { it.assignedTo.id == member.id }
                    val memberCompletedTasks = memberTasks.count { it.status == com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED }
                    val memberPerformance = if (memberTasks.isNotEmpty())
                        (memberCompletedTasks.toFloat() / memberTasks.size * 100).toInt()
                    else 0

                    MemberRow(
                        member = member,
                        isLeader = member.id == leaderId,
                        taskCount = memberTasks.size,
                        performance = memberPerformance,
                        onClick = { onEmployeeClick(member.id) }
                    )
                }

                // Linha de totais/desempenho geral
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Desempenho Geral do Time",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(4.9f)
                    )

                    Text(
                        text = "$teamPerformance%",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            teamPerformance >= 80 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            teamPerformance >= 50 -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                            else -> MaterialTheme.colorScheme.error
                        },
                        modifier = Modifier.weight(0.8f)
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
private fun MemberRow(
    member: Employee,
    isLeader: Boolean,
    taskCount: Int,
    performance: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface,
                RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = member.fullName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        Text(
            text = member.role,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.5f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        Text(
            text = if (isLeader) "Sim" else "Não",
            style = MaterialTheme.typography.bodySmall,
            color = if (isLeader) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isLeader) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(0.8f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        Text(
            text = taskCount.toString(),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.6f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        Text(
            text = "$performance%",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = when {
                performance >= 80 -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
                performance >= 50 -> androidx.compose.ui.graphics.Color(0xFFFFC107)
                else -> MaterialTheme.colorScheme.error
            },
            modifier = Modifier.weight(0.8f)
        )
    }
}

// Tabela de Tarefas dos Colaboradores do Time
@Composable
private fun TeamTasksTable(
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

                HeaderCell("Colaborador", Modifier.weight(1.2f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Data Início", Modifier.weight(0.9f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Prazo", Modifier.weight(0.9f))
                VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

                HeaderCell("Status", Modifier.weight(0.8f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linhas da Tabela
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                tasks.forEach { task ->
                    TeamTaskRow(
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
private fun TeamTaskRow(
    task: Task,
    onTaskClick: (String) -> Unit,
    onProjectClick: (String) -> Unit
) {
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
                .clickable { onProjectClick(task.project.id) },
            fontWeight = FontWeight.Medium
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Nome da Tarefa
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.5f),
            fontWeight = FontWeight.Medium
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Colaborador
        Text(
            text = task.assignedTo.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1.2f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Data Início
        Text(
            text = FormatUtils.formatDate(task.startDate),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.9f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Prazo
        Text(
            text = FormatUtils.formatDate(task.dueDate),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(0.9f)
        )

        VerticalDivider(modifier = Modifier.height(20.dp).padding(horizontal = 4.dp))

        // Status
        Surface(
            color = when (task.status) {
                com.project.taskmanagercivil.domain.models.TaskStatus.TODO -> MaterialTheme.colorScheme.errorContainer
                com.project.taskmanagercivil.domain.models.TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
                com.project.taskmanagercivil.domain.models.TaskStatus.IN_REVIEW -> MaterialTheme.colorScheme.secondaryContainer
                com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
                com.project.taskmanagercivil.domain.models.TaskStatus.BLOCKED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                com.project.taskmanagercivil.domain.models.TaskStatus.INATIVA -> Color(0xFFBDBDBD)
            },
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.weight(0.8f)
        ) {
            Text(
                text = task.status.label,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = when (task.status) {
                    com.project.taskmanagercivil.domain.models.TaskStatus.TODO -> MaterialTheme.colorScheme.onErrorContainer
                    com.project.taskmanagercivil.domain.models.TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.onPrimaryContainer
                    com.project.taskmanagercivil.domain.models.TaskStatus.IN_REVIEW -> MaterialTheme.colorScheme.onSecondaryContainer
                    com.project.taskmanagercivil.domain.models.TaskStatus.COMPLETED -> MaterialTheme.colorScheme.onTertiaryContainer
                    com.project.taskmanagercivil.domain.models.TaskStatus.BLOCKED -> MaterialTheme.colorScheme.onErrorContainer
                    com.project.taskmanagercivil.domain.models.TaskStatus.INATIVA -> Color(0xFF424242)
                }
            )
        }
    }
}

@Composable
private fun getDepartmentColor(department: TeamDepartment): androidx.compose.ui.graphics.Color {
    return when (department) {
        TeamDepartment.ARCHITECTURE -> MaterialTheme.colorScheme.primary
        TeamDepartment.STRUCTURE -> MaterialTheme.colorScheme.secondary
        TeamDepartment.HYDRAULIC -> androidx.compose.ui.graphics.Color(0xFF2196F3)
        TeamDepartment.ELECTRICAL -> androidx.compose.ui.graphics.Color(0xFFFFC107)
        TeamDepartment.MASONRY -> androidx.compose.ui.graphics.Color(0xFF795548)
        TeamDepartment.FINISHING -> androidx.compose.ui.graphics.Color(0xFF9C27B0)
        TeamDepartment.CLEANING -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
        TeamDepartment.SAFETY -> androidx.compose.ui.graphics.Color(0xFFF44336)
        TeamDepartment.ADMINISTRATION -> MaterialTheme.colorScheme.tertiary
        TeamDepartment.PURCHASING -> androidx.compose.ui.graphics.Color(0xFF00BCD4)
        TeamDepartment.QUALITY -> androidx.compose.ui.graphics.Color(0xFF3F51B5)
        TeamDepartment.PLANNING -> androidx.compose.ui.graphics.Color(0xFFFF5722)
    }
}

private fun getDepartmentInitials(department: TeamDepartment): String {
    return when (department) {
        TeamDepartment.ARCHITECTURE -> "AR"
        TeamDepartment.STRUCTURE -> "ES"
        TeamDepartment.HYDRAULIC -> "HI"
        TeamDepartment.ELECTRICAL -> "EL"
        TeamDepartment.MASONRY -> "AL"
        TeamDepartment.FINISHING -> "AC"
        TeamDepartment.CLEANING -> "LI"
        TeamDepartment.SAFETY -> "SE"
        TeamDepartment.ADMINISTRATION -> "AD"
        TeamDepartment.PURCHASING -> "CO"
        TeamDepartment.QUALITY -> "QU"
        TeamDepartment.PLANNING -> "PL"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditGeneralInfoModal(
    team: Team,
    availableEmployees: List<Employee>,
    allEmployees: List<Employee>,
    onDismiss: () -> Unit,
    onSave: (description: String, leaderId: String?, memberIds: List<String>) -> Unit
) {
    var description by remember { mutableStateOf(team.description) }
    var selectedLeaderId by remember { mutableStateOf(team.leaderId ?: "") }
    var selectedMemberIds by remember { mutableStateOf(team.memberIds.toSet()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Informações Gerais") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Descrição / Escopo de Trabalho
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição / Escopo de Trabalho") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )

                // Líder (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = allEmployees.find { it.id == selectedLeaderId }?.fullName ?: "Sem líder",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Líder") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sem líder") },
                            onClick = {
                                selectedLeaderId = ""
                                expanded = false
                            }
                        )
                        allEmployees.forEach { employee ->
                            DropdownMenuItem(
                                text = { Text(employee.fullName) },
                                onClick = {
                                    selectedLeaderId = employee.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Membros (Lista com checkboxes)
                Text(
                    text = "Membros do Time",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allEmployees.forEach { employee ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMemberIds = if (selectedMemberIds.contains(employee.id)) {
                                        selectedMemberIds - employee.id
                                    } else {
                                        selectedMemberIds + employee.id
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedMemberIds.contains(employee.id),
                                onCheckedChange = { checked ->
                                    selectedMemberIds = if (checked) {
                                        selectedMemberIds + employee.id
                                    } else {
                                        selectedMemberIds - employee.id
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = employee.fullName,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        description,
                        selectedLeaderId.ifBlank { null },
                        selectedMemberIds.toList()
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
