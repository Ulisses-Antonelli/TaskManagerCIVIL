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
import androidx.navigation.NavController
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.components.ProjectCard
import com.project.taskmanagercivil.presentation.navigation.NavigationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreenContent(
    navController: NavController,
    viewModel: ProjectsViewModel,
    onProjectClick: (String) -> Unit = {},
    onCreateProject: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val authViewModel = com.project.taskmanagercivil.presentation.ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()
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
            currentRoute = "projects/NONE",
            onMenuClick = onNavigate,
            modifier = Modifier
        )


        Column(modifier = Modifier.weight(1f)) {
            // Estado do modal (antes do Scaffold para ser acessível no TopAppBar)
            var showProjectFormModal by remember { mutableStateOf(false) }
            var projectToEdit by remember { mutableStateOf<com.project.taskmanagercivil.domain.models.Project?>(null) }

            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "TaskManagerCIVIL",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                            }, //---------------------------------------------
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

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DynamicBreadcrumbs(
                                navController = navController,
                                currentRoot = NavigationState.currentRoot,
                                modifier = Modifier.weight(1f)
                            )

                            // Botão de adicionar (alinhado com breadcrumbs)
                            IconButton(
                                onClick = {
                                    projectToEdit = null
                                    showProjectFormModal = true
                                },
                                modifier = Modifier.size(40.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Criar nova obra",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
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
                                onEditProject = { project ->
                                    projectToEdit = project
                                    showProjectFormModal = true
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    // Modal de formulário de projeto
                    if (showProjectFormModal) {
                        ProjectFormModal(
                            project = projectToEdit,
                            onDismiss = {
                                showProjectFormModal = false
                                projectToEdit = null
                            },
                            onSave = { project ->
                                viewModel.saveProject(project)
                                showProjectFormModal = false
                                projectToEdit = null
                            }
                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersRow(
    uiState: ProjectsUiState,
    viewModel: ProjectsViewModel,
    modifier: Modifier = Modifier
) {
    // Estados para controlar abertura dos dropdowns
    var locationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Linha com os 3 dropdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown 1: Localização
            ExposedDropdownMenuBox(
                expanded = locationExpanded,
                onExpandedChange = { locationExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.selectedLocation ?: "Todas",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Localização") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = locationExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = locationExpanded,
                    onDismissRequest = { locationExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            viewModel.onLocationFilterChange(null)
                            locationExpanded = false
                        }
                    )

                    uiState.allProjects.map { it.location }.distinct().sorted().forEach { location ->
                        DropdownMenuItem(
                            text = { Text(location) },
                            onClick = {
                                viewModel.onLocationFilterChange(location)
                                locationExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown 2: Status (Tarefas Internas)
            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = { statusExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.selectedTaskStatus?.label ?: "Todas",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = { statusExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            viewModel.onTaskStatusFilterChange(null)
                            statusExpanded = false
                        }
                    )

                    com.project.taskmanagercivil.domain.models.TaskStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.label) },
                            onClick = {
                                viewModel.onTaskStatusFilterChange(status)
                                statusExpanded = false
                            }
                        )
                    }
                }
            }

            // Dropdown 3: Ordenar por
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.sortBy.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Ordenar por") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sortExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    ProjectSortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                viewModel.onSortByChange(option)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Botão para limpar filtros (se houver filtros ativos)
        if (uiState.selectedLocation != null || uiState.selectedTaskStatus != null) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { viewModel.clearFilters() },
                modifier = Modifier.align(Alignment.End)
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
    onEditProject: (com.project.taskmanagercivil.domain.models.Project) -> Unit,
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
                onClick = { onProjectClick(project.id) },
                onEdit = { onEditProject(project) }
            )
        }
    }
}
