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
import com.project.taskmanagercivil.presentation.components.FinancialTaskRow
import com.project.taskmanagercivil.presentation.navigation.NavigationState

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
    onNavigate: (String) -> Unit = {}
) {
    val authViewModel = com.project.taskmanagercivil.presentation.ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()

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
                            }
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
                    TasksFinancialPanel()

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Painel 2: Obras/Projetos (Nível Meso)
                    ProjectsFinancialPanel()

                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Painel 3: Empresa (Nível Macro)
                    CompanyFinancialPanel()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksFinancialPanel() {
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
                var obraExpanded by remember { mutableStateOf(false) }
                var disciplinaExpanded by remember { mutableStateOf(false) }
                var colaboradorExpanded by remember { mutableStateOf(false) }
                var statusExpanded by remember { mutableStateOf(false) }
                var periodoExpanded by remember { mutableStateOf(false) }

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

            // Tabela de Tarefas com dados mockados
            val mockTasks = listOf(
                FinancialTaskRow(
                    number = 1,
                    taskName = "Projeto Estrutural",
                    projectName = "Ed. Alpha",
                    responsibleName = "Marcos",
                    estimatedDays = 5,
                    actualDays = 4,
                    estimatedCost = 1800.00,
                    actualCost = 1440.00,
                    profitLoss = 360.00,
                    revisions = 0,
                    status = "Concluída"
                ),
                FinancialTaskRow(
                    number = 2,
                    taskName = "Elétrica - 1º Pavto",
                    projectName = "Ed. Alpha",
                    responsibleName = "Carla",
                    estimatedDays = 3,
                    actualDays = 5,
                    estimatedCost = 1100.00,
                    actualCost = 1780.00,
                    profitLoss = -680.00,
                    revisions = 2,
                    status = "Concluída"
                ),
                FinancialTaskRow(
                    number = 3,
                    taskName = "Hidráulica - 2º Pavto",
                    projectName = "Ed. Beta",
                    responsibleName = "João",
                    estimatedDays = 4,
                    actualDays = 4,
                    estimatedCost = 1250.00,
                    actualCost = 1250.00,
                    profitLoss = 0.00,
                    revisions = 1,
                    status = "Em revisão"
                ),
                FinancialTaskRow(
                    number = 4,
                    taskName = "Arquitetônico - Layout",
                    projectName = "Ed. Beta",
                    responsibleName = "Ana",
                    estimatedDays = 6,
                    actualDays = null,
                    estimatedCost = 2900.00,
                    actualCost = null,
                    profitLoss = null,
                    revisions = 0,
                    status = "Em curso"
                ),
                FinancialTaskRow(
                    number = 5,
                    taskName = "PPCI",
                    projectName = "Ed. Gamma",
                    responsibleName = "Pedro",
                    estimatedDays = 2,
                    actualDays = 1,
                    estimatedCost = 900.00,
                    actualCost = 450.00,
                    profitLoss = 450.00,
                    revisions = 0,
                    status = "Concluída"
                ),
                FinancialTaskRow(
                    number = 6,
                    taskName = "Sanitário - Revisão",
                    projectName = "Ed. Alpha",
                    responsibleName = "Carla",
                    estimatedDays = 1,
                    actualDays = 2,
                    estimatedCost = 350.00,
                    actualCost = 700.00,
                    profitLoss = -350.00,
                    revisions = 1,
                    status = "Concluída"
                )
            )

            FinancialTasksTable(
                tasks = mockTasks,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectsFinancialPanel() {
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
                var obraExpanded by remember { mutableStateOf(false) }
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
            var selectedTab by remember { mutableStateOf(0) }
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
            when (selectedTab) {
                0 -> ProjectSummaryTab()
                1 -> ProjectFinancialTab()
                2 -> ProjectTasksTab()
                3 -> ProjectRevisionsTab()
            }
        }
    }
}

@Composable
private fun ProjectRevisionsTab() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Resumo das Revisões
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
                text = "Resumo das Revisões:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Total de Revisões:", "7", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Total de Revisões:", "R$ 5.900,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Impacto no Prazo:", "+14 dias", Modifier.weight(1f), isAlert = true)
            }
        }

        // Revisões por Disciplina
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Revisões por Disciplina:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DisciplineItem("Arquitetura:", "2")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Elétrica:", "3")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Hidráulica:", "2")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Estrutural:", "0")
            }
        }

        // Principais Causas de Revisão
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Principais Causas de Revisão:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RevisionCauseItem(1, "Mudança solicitada pelo cliente", "4")
                HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )
                RevisionCauseItem(2, "Erro de compatibilização", "2")
                HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )
                RevisionCauseItem(3, "Falha de comunicação", "1")
            }
        }
    }
}

@Composable
private fun RevisionCauseItem(number: Int, cause: String, count: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "$number.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = cause,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = "($count)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectTasksTab() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Filtro Disciplina
            var disciplinaExpanded by remember { mutableStateOf(false) }
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
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = disciplinaExpanded,
                    onDismissRequest = { disciplinaExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Todas") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Estrutural") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Elétrica") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Arquitetura") }, onClick = { disciplinaExpanded = false })
                    DropdownMenuItem(text = { Text("Hidráulica") }, onClick = { disciplinaExpanded = false })
                }
            }

            // Filtro Responsável
            var responsavelExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = responsavelExpanded,
                onExpandedChange = { responsavelExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = "Todos",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Responsável", style = MaterialTheme.typography.labelSmall) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = responsavelExpanded) },
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = responsavelExpanded,
                    onDismissRequest = { responsavelExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Todos") }, onClick = { responsavelExpanded = false })
                    DropdownMenuItem(text = { Text("Marcos") }, onClick = { responsavelExpanded = false })
                    DropdownMenuItem(text = { Text("Carla") }, onClick = { responsavelExpanded = false })
                    DropdownMenuItem(text = { Text("Pedro") }, onClick = { responsavelExpanded = false })
                }
            }

            // Filtro Status
            var statusExpanded by remember { mutableStateOf(false) }
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
                    textStyle = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("Todos") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Concluída") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Em curso") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Em revisão") }, onClick = { statusExpanded = false })
                    DropdownMenuItem(text = { Text("Atrasada") }, onClick = { statusExpanded = false })
                }
            }
        }

        // Tabela de Tarefas da Obra
        val projectTasks = listOf(
            FinancialTaskRow(
                number = 1,
                taskName = "Projeto Estrutural",
                projectName = "Estrutural",
                responsibleName = "Marcos",
                estimatedDays = 5,
                actualDays = 4,
                estimatedCost = 1800.00,
                actualCost = 1440.00,
                profitLoss = 360.00,
                revisions = 0,
                status = "Concluída"
            ),
            FinancialTaskRow(
                number = 2,
                taskName = "Elétrica - 1º Pavto",
                projectName = "Elétrica",
                responsibleName = "Carla",
                estimatedDays = 3,
                actualDays = 5,
                estimatedCost = 1100.00,
                actualCost = 1780.00,
                profitLoss = -680.00,
                revisions = 2,
                status = "Concluída"
            ),
            FinancialTaskRow(
                number = 3,
                taskName = "PPCI",
                projectName = "Arquitetura",
                responsibleName = "Pedro",
                estimatedDays = 2,
                actualDays = 1,
                estimatedCost = 900.00,
                actualCost = 450.00,
                profitLoss = 450.00,
                revisions = 0,
                status = "Concluída"
            ),
            FinancialTaskRow(
                number = 4,
                taskName = "Sanitário - Revisão",
                projectName = "Hidráulica",
                responsibleName = "Carla",
                estimatedDays = 1,
                actualDays = 2,
                estimatedCost = 350.00,
                actualCost = 700.00,
                profitLoss = -350.00,
                revisions = 1,
                status = "Concluída"
            )
        )

        // Tabela usando o componente existente
        FinancialTasksTable(
            tasks = projectTasks,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProjectFinancialTab() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Informações Financeiras Principais
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
                text = "Informações Financeiras:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Linha 1: Valores principais
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Valor Contratado:", "R$ 95.000,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Previsto da Obra:", "R$ 62.500,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Custo Real Atual:", "R$ 64.800,00", Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            // Linha 2: Resultados e Margens
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Resultado Previsto:", "+R$ 32.500,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Resultado Atual Projetado:", "+R$ 30.200,00", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Vazio para manter o alinhamento
                }
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            // Linha 3: Margens
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Margem Prevista:", "34%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Margem Atual:", "31.8%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Vazio para manter o alinhamento
                }
            }
        }

        // Custos por Categoria
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Custos por Categoria:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CostCategoryItem("Mão de Obra Interna:", "R$ 49.300,00")
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )
                CostCategoryItem("Retrabalho/Revisões:", "R$ 5.900,00")
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )
                CostCategoryItem("Terceirizados:", "R$ 7.800,00")
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )
                CostCategoryItem("Deslocamentos / Taxas:", "R$ 1.800,00")
            }
        }

        // Curva S (Previsto vs Real)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Curva S (Previsto vs Real):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ProgressBarRow("Previsto:", 0.72f, "72%")
            ProgressBarRow("Real:", 0.64f, "64%")
        }
    }
}

@Composable
private fun CostCategoryItem(category: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun ProjectSummaryTab() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Informações principais da obra
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
                text = "Informações da Obra:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Linha 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Cliente:", "Construtora XYZ", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Responsável Técnico:", "Eng. Marcos", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Início:", "05/08/2025", Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
            )

            // Linha 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoRow("Status:", "Em Execução", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Prazo Previsto:", "90 dias", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                InfoRow("Prazo Real:", "102 dias (-12)", Modifier.weight(1f), isAlert = true)
            }
        }

        // KPIs da Obra
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "KPIs da Obra:",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProjectKPIItem("Progresso Físico:", "68%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Disciplinas:", "4", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Tarefas:", "32 (22 concl. / 10)", Modifier.weight(1f))
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProjectKPIItem("Progresso Financeiro:", "64%", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Eficiência Média:", "1.12x", Modifier.weight(1f))
                VerticalDivider(modifier = Modifier.height(50.dp))
                ProjectKPIItem("Retrabalho:", "9%", Modifier.weight(1f))
            }
        }

        // Progresso (Físico vs Financeiro)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Progresso (Físico vs Financeiro):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ProgressBarRow("Físico:", 0.68f, "68%")
            ProgressBarRow("Financeiro:", 0.64f, "64%")
        }

        // Disciplinas (Distribuição de Tarefas)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Disciplinas (Distribuição de Tarefas):",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DisciplineItem("Arquitetura:", "10")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Estrutural:", "8")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Elétrica:", "7")
                VerticalDivider(modifier = Modifier.height(40.dp))
                DisciplineItem("Hidráulica:", "7")
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, modifier: Modifier = Modifier, isAlert: Boolean = false) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = if (isAlert) Color(0xFFF44336) else MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun ProjectKPIItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun ProgressBarRow(label: String, progress: Float, percentage: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp)
        )
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .weight(1f)
                .height(24.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = percentage,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(50.dp)
        )
    }
}

@Composable
private fun DisciplineItem(label: String, count: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = count,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyFinancialPanel() {
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
                text = "FINANCEIRO DA EMPRESA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Período
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var periodoExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = periodoExpanded,
                    onExpandedChange = { periodoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = "Mês Atual",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Período") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = periodoExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = periodoExpanded,
                        onDismissRequest = { periodoExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mês Atual") },
                            onClick = { periodoExpanded = false }
                        )
                    }
                }
            }

            // KPIs do Mês
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Linha 1 de KPIs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        KPIColumn("Faturamento :", "R$ 142.000", Modifier.weight(1f))
                        VerticalDivider(modifier = Modifier.height(50.dp))
                        KPIColumn("Custos Totais :", "R$ 98.400", Modifier.weight(1f))
                        VerticalDivider(modifier = Modifier.height(50.dp))
                        KPIColumn("Lucro Líquido :", "R$ 43.600", Modifier.weight(1f), valueColor = MaterialTheme.colorScheme.primary)
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
                        KPIColumn("Margem Líquida :", "30.7%", Modifier.weight(1f))
                        VerticalDivider(modifier = Modifier.height(50.dp))
                        KPIColumn("Obras Ativas :", "6", Modifier.weight(1f))
                        VerticalDivider(modifier = Modifier.height(50.dp))
                        KPIColumn("Eficiência Geral :", "1.14x", Modifier.weight(1f))
                    }
                }
            }

            // Receitas x Despesas (estrutura básica)
            Text(
                text = "Receitas x Despesas - Em desenvolvimento",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun KPIColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 8.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
