
package com.project.taskmanagercivil.presentation.screens.tasks

import com.project.taskmanagercivil.presentation.components.NavigationSidebar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.project.taskmanagercivil.domain.models.TaskPriority
import com.project.taskmanagercivil.domain.models.TaskStatus
import com.project.taskmanagercivil.presentation.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreenContent(
    viewModel: TasksViewModel,
    onTaskClick: (com.project.taskmanagercivil.domain.models.Task) -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "tasks",
            onMenuClick = onNavigate,
            modifier = Modifier
        )
        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text("TaskManager Civil")
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    val newMode = if (uiState.viewMode == ViewMode.LIST) ViewMode.KANBAN else ViewMode.LIST
                                    viewModel.onViewModeChange(newMode)
                                }
                            ) {
                                Icon(
                                    imageVector = if (uiState.viewMode == ViewMode.LIST) Icons.Default.ViewModule else Icons.AutoMirrored.Filled.ViewList,
                                    contentDescription = if (uiState.viewMode == ViewMode.LIST) "Visualização Kanban" else "Visualização em Lista"
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Barra de pesquisa
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Filtros de Status
                    StatusFilters(
                        selectedStatus = uiState.selectedStatus,
                        onStatusChange = viewModel::onStatusFilterChange,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Filtros de Prioridade
                    PriorityFilters(
                        selectedPriority = uiState.selectedPriority,
                        onPriorityChange = viewModel::onPriorityFilterChange,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    // Conteúdo principal
                    when {
                        uiState.isLoading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        uiState.error != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = uiState.error!!,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { viewModel.loadTasks() }
                                    ) {
                                        Text("Tentar novamente")
                                    }
                                }
                            }
                        }

                        uiState.filteredTasks.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nenhuma tarefa encontrada",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tente ajustar os filtros ou criar uma nova tarefa",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            TasksList(
                                tasks = uiState.filteredTasks,
                                viewMode = uiState.viewMode,
                                onTaskClick = onTaskClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Pesquisar tarefas...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Pesquisar"
            )
        },
        singleLine = true,
        modifier = modifier
    )
}

@Composable
private fun StatusFilters(
    selectedStatus: TaskStatus?,
    onStatusChange: (TaskStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { onStatusChange(null) },
                label = { Text("Todos") }
            )
        }

        items(TaskStatus.entries) { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusChange(status) },
                label = { Text(status.label) }
            )
        }
    }
}

@Composable
private fun PriorityFilters(
    selectedPriority: TaskPriority?,
    onPriorityChange: (TaskPriority?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedPriority == null,
                onClick = { onPriorityChange(null) },
                label = { Text("Todas Prioridades") }
            )
        }

        items(TaskPriority.entries) { priority ->
            FilterChip(
                selected = selectedPriority == priority,
                onClick = { onPriorityChange(priority) },
                label = { Text(priority.label) }
            )
        }
    }
}

@Composable
private fun TasksList(
    tasks: List<com.project.taskmanagercivil.domain.models.Task>,
    viewMode: ViewMode,
    onTaskClick: (com.project.taskmanagercivil.domain.models.Task) -> Unit,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.LIST -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onClick = { onTaskClick(task) }
                    )
                }
            }
        }

        ViewMode.KANBAN -> {
            KanbanBoard(
                tasks = tasks,
                onTaskClick = onTaskClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun KanbanBoard(
    tasks: List<com.project.taskmanagercivil.domain.models.Task>,
    onTaskClick: (com.project.taskmanagercivil.domain.models.Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (status in TaskStatus.entries) {
            val tasksForStatus = tasks.filter { it.status == status }
            KanbanColumn(
                status = status,
                tasks = tasksForStatus,
                onTaskClick = onTaskClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KanbanColumn(
    status: TaskStatus,
    tasks: List<com.project.taskmanagercivil.domain.models.Task>,
    onTaskClick: (com.project.taskmanagercivil.domain.models.Task) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = status.label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem tarefas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onClick = { onTaskClick(task) }
                        )
                    }
                }
            }
        }
    }
}
