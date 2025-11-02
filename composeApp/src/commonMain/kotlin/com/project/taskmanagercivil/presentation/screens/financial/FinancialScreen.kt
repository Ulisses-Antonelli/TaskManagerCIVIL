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
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    // Título da página
                    Text(
                        text = "Financeiro",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Painel 1: Tarefas (Nível Micro)
                    TasksFinancialPanel()

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )

                    // Painel 2: Obras/Projetos (Nível Meso)
                    ProjectsFinancialPanel()

                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título do painel
            Text(
                text = "PAINEL DE TAREFAS",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                var obraExpanded by remember { mutableStateOf(false) }
                var disciplinaExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = obraExpanded,
                    onExpandedChange = { obraExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Todas",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Obra") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = obraExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = obraExpanded,
                        onDismissRequest = { obraExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = { obraExpanded = false }
                        )
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = disciplinaExpanded,
                    onExpandedChange = { disciplinaExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = "Todas",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Disciplina") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = disciplinaExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = disciplinaExpanded,
                        onDismissRequest = { disciplinaExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Todas") },
                            onClick = { disciplinaExpanded = false }
                        )
                    }
                }
            }

            // KPIs do Período
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    KPIItem("Custo Previsto", "R$ 12.400,00")
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    KPIItem("Custo Real", "R$ 11.980,00")
                    VerticalDivider(modifier = Modifier.height(40.dp))
                    KPIItem("Lucro", "+R$ 420,00", color = MaterialTheme.colorScheme.primary)
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
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título do painel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUADRO DE OBRAS/PROJETOS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Resumo") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Financeiro") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Tarefas") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Revisões") }
                )
            }

            // Conteúdo da aba
            when (selectedTab) {
                0 -> Text("Resumo da Obra - Em desenvolvimento", style = MaterialTheme.typography.bodyMedium)
                1 -> Text("Financeiro da Obra - Em desenvolvimento", style = MaterialTheme.typography.bodyMedium)
                2 -> Text("Tarefas da Obra - Em desenvolvimento", style = MaterialTheme.typography.bodyMedium)
                3 -> Text("Revisões - Em desenvolvimento", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyFinancialPanel() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        KPIItem("Faturamento", "R$ 142.000")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        KPIItem("Custos Totais", "R$ 98.400")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        KPIItem("Lucro Líquido", "R$ 43.600", color = MaterialTheme.colorScheme.primary)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        KPIItem("Margem Líquida", "30.7%")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        KPIItem("Obras Ativas", "6")
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        KPIItem("Eficiência Geral", "1.14x")
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
private fun KPIItem(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
