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
import androidx.compose.material.icons.filled.Search
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
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.navigation.NavigationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreenContent(
    navController: NavController,
    viewModel: TeamsViewModel,
    onTeamClick: (String) -> Unit,
    onCreateTeam: () -> Unit = {},
    onNavigate: (String) -> Unit
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
            onMenuClick = onNavigate
        )

        HorizontalDivider(modifier = Modifier.fillMaxHeight().width(1.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Estado do modal
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
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DynamicBreadcrumbs(
                                navController = navController,
                                currentRoot = NavigationState.currentRoot
                            )

                            Button(
                                onClick = {
                                    teamToEdit = null
                                    showTeamFormModal = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Adicionar")
                                Spacer(Modifier.width(8.dp))
                                Text("Novo Time")
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp)
                ) {
                    // Título
                    Text(
                        text = "Times",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Barra de busca
                    var searchQuery by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        placeholder = { Text("Buscar times...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true
                    )

                    // Lista de times
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val filteredTeams = uiState.teams.filter {
                            it.name.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredTeams.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Nenhum time encontrado",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredTeams) { team ->
                                    TeamCard(
                                        team = team,
                                        onClick = { onTeamClick(team.id) },
                                        onEdit = {
                                            teamToEdit = team
                                            showTeamFormModal = true
                                        },
                                        onDelete = { viewModel.deleteTeam(team.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Modal de formulário
            if (showTeamFormModal) {
                TeamFormModal(
                    team = teamToEdit,
                    onDismiss = { showTeamFormModal = false },
                    onSave = { team ->
                        viewModel.saveTeam(team)
                        showTeamFormModal = false
                    }
                )
            }
        }
    }
}

@Composable
private fun TeamCard(
    team: Team,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = team.description ?: "Sem descrição",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${team.memberIds.size} membros",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Mais opções")
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
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Excluir") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}
