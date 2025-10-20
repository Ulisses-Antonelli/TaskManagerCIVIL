package com.project.taskmanagercivil.presentation.screens.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.components.ProjectCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreenContent(
    viewModel: ProjectsViewModel,
    onProjectClick: (String) -> Unit = {},
    onCreateProject: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "projects",
            onMenuClick = onNavigate,
            modifier = Modifier
        )

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.weight(1f)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Obras e Projetos") },
                        actions = {
                            // Botão de adicionar (futuro)
                            IconButton(onClick = onCreateProject) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar Projeto"
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


                    FiltersRow(
                        uiState = uiState,
                        viewModel = viewModel,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    
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
                                    Button(onClick = { viewModel.loadData() }) {
                                        Text("Tentar novamente")
                                    }
                                }
                            }
                        }

                        uiState.filteredProjects.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nenhum projeto encontrado",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Tente ajustar os filtros",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            ProjectsList(
                                projects = uiState.filteredProjects,
                                onProjectClick = onProjectClick,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Pesquisar projetos...") },
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
private fun FiltersRow(
    uiState: ProjectsUiState,
    viewModel: ProjectsViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Filtro de localização
        Text(
            text = "Localização",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedLocation == null,
                    onClick = { viewModel.onLocationFilterChange(null) },
                    label = { Text("Todas") }
                )
            }

            items(uiState.allProjects.map { it.location }.distinct().sorted()) { location ->
                FilterChip(
                    selected = uiState.selectedLocation == location,
                    onClick = { viewModel.onLocationFilterChange(location) },
                    label = { Text(location) }
                )
            }
        }

        // Filtro por status de tarefas internas
        Text(
            text = "Tarefas Internas",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedTaskStatus == null,
                    onClick = { viewModel.onTaskStatusFilterChange(null) },
                    label = { Text("Todas") }
                )
            }

            items(com.project.taskmanagercivil.domain.models.TaskStatus.entries.toList()) { status ->
                FilterChip(
                    selected = uiState.selectedTaskStatus == status,
                    onClick = { viewModel.onTaskStatusFilterChange(status) },
                    label = { Text(status.label) }
                )
            }
        }

        // Ordenação
        Text(
            text = "Ordenar por",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ProjectSortOption.entries) { option ->
                FilterChip(
                    selected = uiState.sortBy == option,
                    onClick = { viewModel.onSortByChange(option) },
                    label = { Text(option.label) }
                )
            }
        }

        // Botão para limpar filtros
        if (uiState.selectedLocation != null || uiState.selectedTaskStatus != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.clearFilters() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpar Filtros")
            }
        }
    }
}

@Composable
private fun ProjectsList(
    projects: List<com.project.taskmanagercivil.domain.models.Project>,
    onProjectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(projects, key = { it.id }) { project ->
            ProjectCard(
                project = project,
                onClick = { onProjectClick(project.id) }
            )
        }
    }
}
