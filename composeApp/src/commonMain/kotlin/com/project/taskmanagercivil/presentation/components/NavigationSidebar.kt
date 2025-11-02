package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import taskmanagercivil.composeapp.generated.resources.Logo_TMC
import taskmanagercivil.composeapp.generated.resources.Res


val baseMenuItems = listOf(
    "dashboard" to "Dashboard",
    "projects/NONE" to "Obras/Projetos",  // Navegação sem filtro
    "tasks" to "Tarefas",
    "users" to "Colaboradores",
    "teams" to "Times",
    "documents" to "Documentos",
    "reports" to "Relatórios",
    "settings" to "Configurações"
)

val adminOnlyMenuItems = listOf(
    "user_management" to "Gerenciar Usuários"
)

@Composable
fun NavigationSidebar(
    currentRoute: String?,
    onMenuClick: (String) -> Unit,
    currentUser: com.project.taskmanagercivil.domain.models.User? = null,
    modifier: Modifier = Modifier
) {
    // Versão simplificada - sempre mostra sidebar
    Surface(
        modifier = modifier.width(220.dp).fillMaxHeight(),
        tonalElevation = 2.dp
    ) {
        SidebarContent(currentRoute, onMenuClick, currentUser)
    }
}

@Composable
private fun SidebarContent(
    currentRoute: String?,
    onMenuClick: (String) -> Unit,
    currentUser: com.project.taskmanagercivil.domain.models.User?
) {
    // Obtém o usuário autenticado do AuthViewModel global
    val authViewModel = com.project.taskmanagercivil.presentation.ViewModelFactory.getAuthViewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Usa o usuário autenticado ou o fornecido como parâmetro (para compatibilidade)
    val userToUse = currentUser ?: authState.currentUser

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Logo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.Logo_TMC),
                contentDescription = "Logo TMC",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(80.dp)
            )
        }

        // Menu items padrão
        for ((route, label) in baseMenuItems) {
            // Para Projects, verifica se a rota atual começa com "projects/"
            // Isso permite destacar o item mesmo com filtros diferentes (projects/TODO, projects/NONE, etc)
            val isSelected = if (route.startsWith("projects/")) {
                currentRoute?.startsWith("projects/") == true
            } else {
                currentRoute == route
            }

            ModernNavigationItem(
                label = label,
                selected = isSelected,
                onClick = { onMenuClick(route) }
            )
        }

        // Divider antes dos menus administrativos
        if (com.project.taskmanagercivil.domain.models.PermissionChecker.isAdmin(userToUse)) {
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Menu items apenas para ADMIN
            for ((route, label) in adminOnlyMenuItems) {
                val isSelected = currentRoute == route

                ModernNavigationItem(
                    label = label,
                    selected = isSelected,
                    onClick = { onMenuClick(route) }
                )
            }
        }
    }
}

@Composable
private fun ModernNavigationItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)) // Cantos levemente arredondados (8dp)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = if (selected) {
                androidx.compose.ui.text.font.FontWeight.Medium
            } else {
                androidx.compose.ui.text.font.FontWeight.Normal
            }
        )
    }
}
