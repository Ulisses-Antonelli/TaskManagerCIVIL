package com.project.taskmanagercivil.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.components.dashboard.*

/**
 * Tela de Dashboard com visão geral do sistema
 */
@Composable
fun DashboardScreenContent(
    viewModel: DashboardViewModel,
    onNavigate: (String) -> Unit = {},
    onProjectClick: (String) -> Unit = {},
    onTaskClick: (String) -> Unit = {},
    onProjectsWithStatusClick: (TaskStatus) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "dashboard",
            onMenuClick = onNavigate
        )

        HorizontalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Scaffold(
                topBar = {
                    DashboardTopBar(
                        onRefresh = { viewModel.refresh() }
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
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = uiState.error ?: "Erro desconhecido",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Button(onClick = { viewModel.refresh() }) {
                                    Text("Tentar Novamente")
                                }
                            }
                        }
                    }

                    else -> {
                        DashboardContent(
                            uiState = uiState,
                            onProjectClick = onProjectClick,
                            onTaskClick = onTaskClick,
                            onProjectsWithStatusClick = onProjectsWithStatusClick,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Dashboard,
                    contentDescription = "Dashboard",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        actions = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun DashboardContent(
    uiState: DashboardUiState,
    onProjectClick: (String) -> Unit,
    onTaskClick: (String) -> Unit,
    onProjectsWithStatusClick: (TaskStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Seção 1: Cards de resumo de status
        Text(
            text = "Resumo de Obras",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusSummaryCard(
                status = TaskStatus.TODO,
                count = uiState.dashboardData.projectStatusSummary.todoCount,
                onClick = { onProjectsWithStatusClick(TaskStatus.TODO) },
                modifier = Modifier.weight(1f)
            )
            StatusSummaryCard(
                status = TaskStatus.IN_PROGRESS,
                count = uiState.dashboardData.projectStatusSummary.inProgressCount,
                onClick = { onProjectsWithStatusClick(TaskStatus.IN_PROGRESS) },
                modifier = Modifier.weight(1f)
            )
            StatusSummaryCard(
                status = TaskStatus.IN_REVIEW,
                count = uiState.dashboardData.projectStatusSummary.inReviewCount,
                onClick = { onProjectsWithStatusClick(TaskStatus.IN_REVIEW) },
                modifier = Modifier.weight(1f)
            )
            StatusSummaryCard(
                status = TaskStatus.COMPLETED,
                count = uiState.dashboardData.projectStatusSummary.completedCount,
                onClick = { onProjectsWithStatusClick(TaskStatus.COMPLETED) },
                modifier = Modifier.weight(1f)
            )
            StatusSummaryCard(
                status = TaskStatus.BLOCKED,
                count = uiState.dashboardData.projectStatusSummary.blockedCount,
                onClick = { onProjectsWithStatusClick(TaskStatus.BLOCKED) },
                modifier = Modifier.weight(1f)
            )
        }

        // Seção 2: Gráfico de progresso e Prazos críticos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProgressChart(
                progressStats = uiState.dashboardData.progressStats,
                onClick = { onProjectsWithStatusClick(TaskStatus.IN_PROGRESS) },
                modifier = Modifier.weight(1f)
            )

            CriticalDeadlinesCard(
                criticalDeadlines = uiState.dashboardData.criticalDeadlines,
                onDeadlineClick = onTaskClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Seção 3: Indicadores Financeiros
        FinancialIndicatorsCard(
            financialIndicators = uiState.dashboardData.financialIndicators,
            onProjectClick = onProjectClick
        )

        // Seção 4: Gráfico de projetos por mês
        MonthlyProjectChart(
            monthlyData = uiState.dashboardData.monthlyProjectData,
            onClick = { onProjectsWithStatusClick(TaskStatus.IN_PROGRESS) }
        )

        // Espaçamento final
        Spacer(modifier = Modifier.height(24.dp))
    }
}
