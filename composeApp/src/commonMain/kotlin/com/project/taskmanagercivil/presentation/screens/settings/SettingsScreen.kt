package com.project.taskmanagercivil.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.taskmanagercivil.presentation.ViewModelFactory
import com.project.taskmanagercivil.presentation.components.DynamicBreadcrumbs
import com.project.taskmanagercivil.presentation.components.NavigationSidebar
import com.project.taskmanagercivil.presentation.components.ThemeSelectionModal
import com.project.taskmanagercivil.presentation.navigation.NavigationState

@Composable
fun SettingsScreenContent(
    navController: NavController,
    onNavigate: (String) -> Unit = {}
) {
    val authViewModel = ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()

    val themeViewModel = ViewModelFactory.getThemeViewModel()
    val themeSettings by themeViewModel.themeSettings.collectAsState()

    var showThemeModal by remember { mutableStateOf(false) }
    var logoutRequested by remember { mutableStateOf(false) }

    // Observa mudanças no estado de autenticação
    LaunchedEffect(authState.isAuthenticated) {
        if (!authState.isAuthenticated && logoutRequested) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
            logoutRequested = false
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        NavigationSidebar(
            currentRoute = NavigationState.currentRoot,
            onMenuClick = { route ->
                when (route) {
                    "logout" -> {
                        logoutRequested = true
                        authViewModel.logout()
                    }
                    else -> onNavigate(route)
                }
            },
            currentUser = authState.currentUser
        )

        // Conteúdo principal
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Breadcrumbs
            DynamicBreadcrumbs(
                navController = navController,
                currentRoot = "settings"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título
            Text(
                text = "Configurações",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Personalize sua experiência na aplicação",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Card de Tema
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Aparência",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tema atual: ${themeSettings.selectedTheme.displayName}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { showThemeModal = true },
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Trocar Tema")
                        }
                    }

                    HorizontalDivider()

                    Text(
                        text = "Escolha entre diferentes paletas de cores para personalizar a aparência da aplicação de acordo com sua preferência.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Informações adicionais
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "O tema selecionado será aplicado imediatamente em toda a aplicação",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Modal de seleção de tema
    if (showThemeModal) {
        ThemeSelectionModal(
            currentTheme = themeSettings.selectedTheme,
            onDismiss = { showThemeModal = false },
            onThemeSelected = { theme ->
                themeViewModel.changeTheme(theme)
            }
        )
    }
}
