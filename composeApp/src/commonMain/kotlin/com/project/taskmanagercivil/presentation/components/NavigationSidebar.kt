package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp


val menuItems = listOf(
    "dashboard" to "Dashboard",
    "projects" to "Obras/Projetos",
    "tasks" to "Tarefas",
    "users" to "Colaboradores",
    "teams" to "Times",
    "documents" to "Documentos",
    "reports" to "Relatórios",
    "settings" to "Configurações"
)

@Composable
fun NavigationSidebar(
    currentRoute: String?,
    onMenuClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Versão simplificada - sempre mostra sidebar
    Surface(
        modifier = modifier.width(220.dp).fillMaxHeight(),
        tonalElevation = 2.dp
    ) {
        SidebarContent(currentRoute, onMenuClick)
    }
}

@Composable
private fun SidebarContent(
    currentRoute: String?,
    onMenuClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(horizontal = 12.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for ((route, label) in menuItems) {
            ModernNavigationItem(
                label = label,
                selected = currentRoute == route,
                onClick = { onMenuClick(route) }
            )
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
