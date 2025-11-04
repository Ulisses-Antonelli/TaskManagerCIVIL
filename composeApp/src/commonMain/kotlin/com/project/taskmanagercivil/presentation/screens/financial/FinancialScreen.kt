package com.project.taskmanagercivil.presentation.screens.financial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.components.FinancialTasksTable
import com.project.taskmanagercivil.presentation.components.KPIColumn
import com.project.taskmanagercivil.presentation.navigation.NavigationState
import com.project.taskmanagercivil.presentation.screens.financial.tabs.ProjectSummaryTab
import com.project.taskmanagercivil.presentation.screens.financial.tabs.ProjectFinancialTab
import com.project.taskmanagercivil.presentation.screens.financial.tabs.ProjectTasksTab
import com.project.taskmanagercivil.presentation.screens.financial.tabs.ProjectRevisionsTab
import com.project.taskmanagercivil.presentation.screens.financial.CompanyFinancialPanel

/**
 * Tela de Financeiro com 3 níveis de indicadores:
 * 1. Painel de Tarefas (nível micro)
 * 2. Painel de Obras/Projetos (nível meso)
 * 3. Painel Financeiro da Empresa (nível macro)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialScreenContent(
    navController: NavController,
    viewModel: FinancialViewModel,
    onNavigate: (String) -> Unit = {}
) {
    val authViewModel = com.project.taskmanagercivil.presentation.ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Estado do ViewModel
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

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "financial",
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
                        DynamicBreadcrumbs(
                            navController = navController,
                            currentRoot = NavigationState.currentRoot
                        )
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Título da página
                    Text(
                        text = "Financeiro",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    // Painel 1: Tarefas (Nível Micro)
                    TasksFinancialPanel(
                        tasks = uiState.tasksPanelTasks,
                        onFilterChange = { project, discipline, responsible, status, period ->
                            viewModel.onFilterChange(project, discipline, responsible, status, period)
                        }
                    )

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Painel 2: Obras/Projetos (Nível Meso)
                    ProjectsFinancialPanel(
                        projectFinancials = uiState.projectFinancials,
                        onProjectSelected = { projectId -> viewModel.onProjectSelected(projectId) }
                    )

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Painel 3: Empresa (Nível Macro)
                    CompanyFinancialPanel(
                        companyFinancials = uiState.companyFinancials,
                        onPeriodChange = { period -> viewModel.onCompanyPeriodChange(period) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksFinancialPanel(
    tasks: List<com.project.taskmanagercivil.domain.models.FinancialTask>,
    onFilterChange: (String?, String?, String?, String?, String?) -> Unit
) {
    var obraExpanded by remember { mutableStateOf(false) }
    var disciplinaExpanded by remember { mutableStateOf(false) }
    var colaboradorExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var periodoExpanded by remember { mutableStateOf(false) }

    var selectedObra by remember { mutableStateOf<String?>(null) }
    var selectedDisciplina by remember { mutableStateOf<String?>(null) }
    var selectedColaborador by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var selectedPeriodo by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título do painel
            Text(
                text = "PAINEL DE TAREFAS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Filtros em UMA linha: Obra, Disciplina, Colaborador, Status, Período
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                // Filtro: Obra
                ExposedDropdownMenuBox(
                    expanded = obraExpanded,
                    onExpandedChange = { obraExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Todas",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Obra", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = obraExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(
                        expanded = obraExpanded,
                        onDismissRequest = { obraExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Todas") }, onClick = { obraExpanded = false })
                        DropdownMenuItem(text = { Text("Ed. Alpha") }, onClick = { obraExpanded = false })
                        DropdownMenuItem(text = { Text("Ed. Beta") }, onClick = { obraExpanded = false })
                        DropdownMenuItem(text = { Text("Ed. Gamma") }, onClick = { obraExpanded = false })
                    }
                }

                // Filtro: Disciplina
                ExposedDropdownMenuBox(
                    expanded = disciplinaExpanded,
                    onExpandedChange = { disciplinaExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Todas",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Disciplina", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disciplinaExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(
                        expanded = disciplinaExpanded,
                        onDismissRequest = { disciplinaExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Todas") }, onClick = { disciplinaExpanded = false })
                        DropdownMenuItem(text = { Text("Arquitetura") }, onClick = { disciplinaExpanded = false })
                        DropdownMenuItem(text = { Text("Estrutural") }, onClick = { disciplinaExpanded = false })
                        DropdownMenuItem(text = { Text("Elétrica") }, onClick = { disciplinaExpanded = false })
                        DropdownMenuItem(text = { Text("Hidráulica") }, onClick = { disciplinaExpanded = false })
                    }
                }

                // Filtro: Colaborador
                ExposedDropdownMenuBox(
                    expanded = colaboradorExpanded,
                    onExpandedChange = { colaboradorExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Todos",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Colaborador", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = colaboradorExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(
                        expanded = colaboradorExpanded,
                        onDismissRequest = { colaboradorExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Todos") }, onClick = { colaboradorExpanded = false })
                        DropdownMenuItem(text = { Text("Marcos") }, onClick = { colaboradorExpanded = false })
                        DropdownMenuItem(text = { Text("Carla") }, onClick = { colaboradorExpanded = false })
                        DropdownMenuItem(text = { Text("João") }, onClick = { colaboradorExpanded = false })
                        DropdownMenuItem(text = { Text("Ana") }, onClick = { colaboradorExpanded = false })
                        DropdownMenuItem(text = { Text("Pedro") }, onClick = { colaboradorExpanded = false })
                    }
                }

                // Filtro: Status
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Todos",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Todos") }, onClick = { statusExpanded = false })
                        DropdownMenuItem(text = { Text("Em curso") }, onClick = { statusExpanded = false })
                        DropdownMenuItem(text = { Text("Em revisão") }, onClick = { statusExpanded = false })
                        DropdownMenuItem(text = { Text("Concluída") }, onClick = { statusExpanded = false })
                        DropdownMenuItem(text = { Text("Atrasada") }, onClick = { statusExpanded = false })
                    }
                }

                // Filtro: Período (dropdown com períodos pré-definidos)
                ExposedDropdownMenuBox(
                    expanded = periodoExpanded,
                    onExpandedChange = { periodoExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Este Mês",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Período", style = MaterialTheme.typography.labelSmall) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodoExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    ExposedDropdownMenu(
                        expanded = periodoExpanded,
                        onDismissRequest = { periodoExpanded = false }
                    ) {
                        DropdownMenuItem(text = { Text("Hoje") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Esta Semana") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Este Mês") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Último Mês") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Últimos 3 Meses") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Este Ano") }, onClick = { periodoExpanded = false })
                        DropdownMenuItem(text = { Text("Personalizado") }, onClick = { periodoExpanded = false })
                    }
                }
            }

            // KPIs do Período
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "KPIs do Período Selecionado:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                // Linha 1 de KPIs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("Custo Previsto :", "R$ 12.400,00", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Custo Real :", "R$ 11.980,00", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Lucro :", "+R$ 420,00", Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.primary)
                }

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )

                // Linha 2 de KPIs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    KPIColumn("% Entregue no Prazo :", "72%", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Revisões :", "8", Modifier.weight(1f))
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    KPIColumn("Eficiência Média :", "1.08x", Modifier.weight(1f))
                }
            }

            // Tabela de Tarefas
            FinancialTasksTable(
                tasks = tasks.toTaskRows(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectsFinancialPanel(
    projectFinancials: com.project.taskmanagercivil.domain.models.ProjectFinancials?,
    onProjectSelected: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var obraExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cabeçalho com título e seletor
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAINEL DE OBRAS/PROJETOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Seletor de obra
                ExposedDropdownMenuBox(
                    expanded = obraExpanded,
                    onExpandedChange = { obraExpanded = it }
                ) {
                    OutlinedButton(
                        onClick = { obraExpanded = true },
                        modifier = Modifier.menuAnchor()
                    ) {
                        Text("Edifício Alpha")
                        Icon(Icons.Default.ArrowDropDown, null)
                    }
                    ExposedDropdownMenu(
                        expanded = obraExpanded,
                        onDismissRequest = { obraExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edifício Alpha") },
                            onClick = { obraExpanded = false }
                        )
                    }
                }
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                contentColor = MaterialTheme.colorScheme.onSurface,
                indicator = { tabPositions ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(Alignment.BottomStart)
                            .offset(x = tabPositions[selectedTab].left)
                            .width(tabPositions[selectedTab].width)
                            .height(3.dp)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Resumo",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.background(
                        if (selectedTab == 0)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Financeiro",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.background(
                        if (selectedTab == 1)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Tarefas",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.background(
                        if (selectedTab == 2)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Text(
                            "Revisões",
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.background(
                        if (selectedTab == 3)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            }

            // Conteúdo da aba
            if (projectFinancials != null) {
                when (selectedTab) {
                    0 -> ProjectSummaryTab(projectFinancials)
                    1 -> ProjectFinancialTab(projectFinancials)
                    2 -> ProjectTasksTab(projectFinancials)
                    3 -> ProjectRevisionsTab(projectFinancials)
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


