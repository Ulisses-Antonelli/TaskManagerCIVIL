package com.project.taskmanagercivil.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.domain.models.PermissionChecker
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.navigation.NavigationState
//import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Tela de Gerenciamento de Usuários
 * Acessível apenas para ADMIN
 * Permite criar, editar, desativar usuários e atribuir papéis
 */
@OptIn(ExperimentalMaterial3Api::class)


@Preview
@Composable
fun UserManagementScreen(
    navController: NavController,
    currentUser: User?,
    viewModel: UserManagementViewModel = remember { com.project.taskmanagercivil.presentation.ViewModelFactory.createUserManagementViewModel() }
) {
    val authViewModel = com.project.taskmanagercivil.presentation.ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()

    val uiState by viewModel.uiState.collectAsState()
    var showUserFormDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<User?>(null) }

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

    // Verificar permissão de acesso
    if (!PermissionChecker.canManageUsers(currentUser)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Acesso Negado",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Apenas administradores podem acessar esta tela.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { navController.navigateUp() }
                ) {
                    Text("Voltar")
                }
            }
        }
        return
    }

    Row(modifier = Modifier.fillMaxSize()) {
        NavigationSidebar(
            currentRoute = "user_management",
            onMenuClick = { route -> navController.navigate(route) },
            modifier = Modifier
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

                        // Breadcrumbs + filtros + botão de adicionar
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
                                        selectedUser = null
                                        showUserFormDialog = true
                                    },
                                    modifier = Modifier.size(40.dp),
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Criar novo usuário",
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            // Filtros abaixo dos breadcrumbs
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = uiState.selectedRoleFilter == null,
                                    onClick = { viewModel.setRoleFilter(null) },
                                    label = { Text("Todos") }
                                )
                                Role.entries.forEach { role ->
                                    FilterChip(
                                        selected = uiState.selectedRoleFilter == role,
                                        onClick = { viewModel.setRoleFilter(role) },
                                        label = { Text(role.displayName) }
                                    )
                                }
                            }
                        }
                    }
                }
            ) { paddingValues ->
                // Tabela de usuários
                com.project.taskmanagercivil.presentation.components.UserManagementTable(
                    users = uiState.filteredUsers,
                    onUserClick = { userId ->
                        val user = uiState.users.find { it.id == userId }
                        if (user != null && currentUser?.id != user.id) {
                            selectedUser = user
                            showUserFormDialog = true
                        }
                    },
                    onRolesChange = { userId, newRoles ->
                        viewModel.updateUserRoles(userId, newRoles)
                    },
                    onStatusChange = { userId, isActive ->
                        val user = uiState.users.find { it.id == userId }
                        if (user != null && currentUser?.id != user.id) {
                            viewModel.setUserActive(user, isActive)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }

    // Dialog de formulário de usuário
    if (showUserFormDialog) {
        UserFormDialog(
            user = selectedUser,
            onDismiss = {
                showUserFormDialog = false
                selectedUser = null
            },
            onSave = { user ->
                viewModel.saveUser(user)
                showUserFormDialog = false
                selectedUser = null
            }
        )
    }
}

@Composable
private fun UserCard(
    user: User,
    onEditClick: () -> Unit,
    onToggleActiveClick: () -> Unit,
    canEdit: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            colors = if (!user.isActive) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.name.first().uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Informações do usuário
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!user.isActive) {
                        Surface(
                            color = MaterialTheme.colorScheme.error,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "INATIVO",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError
                            )
                        }
                    }
                }

                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Papéis
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    user.roles.forEach { role ->
                        RoleBadge(role)
                    }
                }
            }

            // Ações
            if (canEdit) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar usuário"
                        )
                    }
                    IconButton(onClick = onToggleActiveClick) {
                        Icon(
                            if (user.isActive) Icons.Default.Lock else Icons.Default.LockOpen,
                            contentDescription = if (user.isActive) "Desativar" else "Ativar",
                            tint = if (user.isActive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: Role) {
    val backgroundColor = when (role) {
        Role.ADMIN -> Color(0xFFD32F2F)
        Role.GESTOR_OBRAS -> Color(0xFF1976D2)
        Role.LIDER_EQUIPE -> Color(0xFFFFA726)
        Role.FUNCIONARIO -> Color(0xFF757575)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = role.displayName,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}



