package com.project.taskmanagercivil.presentation.screens.teams

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.Team
import com.project.taskmanagercivil.domain.models.TeamDepartment
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.navigation.NavigationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent(
    navController: NavController,
    viewModel: TeamsViewModel,
    onTeamClick: (String) -> Unit = {},
    onCreateTeam: () -> Unit = {},
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
            currentRoute = "teams",
            onMenuClick = onNavigate,
            modifier = Modifier
        )

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Estado do modal (antes do Scaffold para ser acessível no TopAppBar)
            var showTeamFormModal by remember { mutableStateOf(false) }
            var teamToEdit by remember { mutableStateOf<Team?>(null) }

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
                                    teamToEdit = null
                                    showTeamFormModal = true
                                },
                                modifier = Modifier.size(40.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Criar nova equipe",
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

                        uiState.teams.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nenhuma equipe encontrada",
                                        style = MaterialTheme.typography.headlineSmall
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Crie sua primeira equipe",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        else -> {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = uiState.filteredTeams,
                                    key = { it.id }
                                ) { team ->
                                    TeamCard(
                                        team = team,
                                        onClick = { onTeamClick(team.id) },
                                        onEdit = {
                                            teamToEdit = team
                                            showTeamFormModal = true
                                        }
                                    )                                  
                                }
                            }                         
                        }
                    }
                    // Modal de formulário de equipe
                    if (showTeamFormModal) {
                        TeamFormModal(
                            team = teamToEdit,
                            availableEmployees = uiState.allEmployees,
                            availableProjects = uiState.allProjects,
                            onDismiss = {
                                showTeamFormModal = false
                                teamToEdit = null
                            },
                            onSave = { team ->
                                viewModel.saveTeam(team)
                                showTeamFormModal = false
                                teamToEdit = null
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
        modifier = modifier,
        placeholder = { Text("Buscar por nome ou descrição...") },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)      
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersRow(
    uiState: TeamsUiState,
    viewModel: TeamsViewModel,
    modifier: Modifier = Modifier
) {
    // Estados para controlar abertura dos dropdowns
    var departmentExpanded by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // Linha com os 2 dropdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dropdown 1: Departamento
            ExposedDropdownMenuBox(
                expanded = departmentExpanded,
                onExpandedChange = { departmentExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = when (uiState.filterDepartment) {
                        TeamFilterDepartment.ALL -> "Todos os Departamentos"
                        TeamFilterDepartment.ARCHITECTURE -> TeamDepartment.ARCHITECTURE.displayName
                        TeamFilterDepartment.STRUCTURE -> TeamDepartment.STRUCTURE.displayName
                        TeamFilterDepartment.HYDRAULIC -> TeamDepartment.HYDRAULIC.displayName
                        TeamFilterDepartment.ELECTRICAL -> TeamDepartment.ELECTRICAL.displayName
                        TeamFilterDepartment.MASONRY -> TeamDepartment.MASONRY.displayName
                        TeamFilterDepartment.FINISHING -> TeamDepartment.FINISHING.displayName
                        TeamFilterDepartment.CLEANING -> TeamDepartment.CLEANING.displayName
                        TeamFilterDepartment.SAFETY -> TeamDepartment.SAFETY.displayName
                        TeamFilterDepartment.ADMINISTRATION -> TeamDepartment.ADMINISTRATION.displayName
                        TeamFilterDepartment.PURCHASING -> TeamDepartment.PURCHASING.displayName
                        TeamFilterDepartment.QUALITY -> TeamDepartment.QUALITY.displayName
                        TeamFilterDepartment.PLANNING -> TeamDepartment.PLANNING.displayName
                    },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Departamento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = departmentExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = departmentExpanded,
                    onDismissRequest = { departmentExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todos os Departamentos") },
                        onClick = {
                            viewModel.onFilterDepartmentChange(TeamFilterDepartment.ALL)
                            departmentExpanded = false
                        }
                    )
                    TeamFilterDepartment.entries.forEach { filter ->
                        if (filter != TeamFilterDepartment.ALL) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (filter) {
                                            TeamFilterDepartment.ARCHITECTURE -> TeamDepartment.ARCHITECTURE.displayName
                                            TeamFilterDepartment.STRUCTURE -> TeamDepartment.STRUCTURE.displayName
                                            TeamFilterDepartment.HYDRAULIC -> TeamDepartment.HYDRAULIC.displayName
                                            TeamFilterDepartment.ELECTRICAL -> TeamDepartment.ELECTRICAL.displayName
                                            TeamFilterDepartment.MASONRY -> TeamDepartment.MASONRY.displayName
                                            TeamFilterDepartment.FINISHING -> TeamDepartment.FINISHING.displayName
                                            TeamFilterDepartment.CLEANING -> TeamDepartment.CLEANING.displayName
                                            TeamFilterDepartment.SAFETY -> TeamDepartment.SAFETY.displayName
                                            TeamFilterDepartment.ADMINISTRATION -> TeamDepartment.ADMINISTRATION.displayName
                                            TeamFilterDepartment.PURCHASING -> TeamDepartment.PURCHASING.displayName
                                            TeamFilterDepartment.QUALITY -> TeamDepartment.QUALITY.displayName
                                            TeamFilterDepartment.PLANNING -> TeamDepartment.PLANNING.displayName
                                            else -> ""
                                        }
                                    )
                                },
                                onClick = {
                                    viewModel.onFilterDepartmentChange(filter)
                                    departmentExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Dropdown 2: Ordenar por
            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = uiState.sortOrder.displayName,
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
                    TeamSortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.displayName) },
                            onClick = {
                                viewModel.onSortOrderChange(order)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Botão para limpar filtros (se houver filtros ativos)
        if (uiState.filterDepartment != TeamFilterDepartment.ALL) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = { viewModel.onFilterDepartmentChange(TeamFilterDepartment.ALL) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Limpar Filtros")
            }
        }
    }
}

@Composable
private fun TeamCard(
    team: Team,
    onClick: () -> Unit,
    onEdit: ((Team) -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar com iniciais do departamento (mantém colorido)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(getDepartmentColor(team.department)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getDepartmentInitials(team.department),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Coluna 1: Nome (maior) e Descrição
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = team.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Badge de Inativo (se aplicável)
                    if (!team.isActive) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "Inativo",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                if (team.description.isNotEmpty()) {
                    Text(
                        text = team.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Divisor vertical 1
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 2: Departamento
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Departamento",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = getDepartmentColor(team.department).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = team.department.displayName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = getDepartmentColor(team.department),
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            // Divisor vertical 2
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 3: Líder
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Líder",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = team.leaderId ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Divisor vertical 3
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 4: Membros
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Membros",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = team.getTotalMembers().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            // Divisor vertical 4
            VerticalDivider(
                modifier = Modifier
                    .height(56.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Coluna 5: Projetos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Projetos",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = team.getTotalProjects().toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

            // Botão Editar no canto superior direito (sobreposto)
            if (onEdit != null) {
                var showMenu by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Mais opções",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                                onEdit(team)
                            }
                        )
                    }
                }
            }
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