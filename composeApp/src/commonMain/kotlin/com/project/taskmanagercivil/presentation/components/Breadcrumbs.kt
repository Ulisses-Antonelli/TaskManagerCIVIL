package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Data class para representar um item no breadcrumb
 */
data class BreadcrumbItem(
    val label: String,
    val icon: ImageVector? = null,
    val route: String? = null,
    val isActive: Boolean = false
)

/**
 * Componente de Breadcrumbs dinâmico baseado em NavController
 *
 * @param navController Controller de navegação para rastrear backstack
 * @param modifier Modificador opcional
 */
@Composable
fun DynamicBreadcrumbs(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    // Constrói lista de breadcrumbs baseado na rota atual
    val breadcrumbItems = remember(currentRoute) {
        buildBreadcrumbsFromRoute(currentRoute)
    }

    Breadcrumbs(
        items = breadcrumbItems,
        onNavigate = { route ->
            // Navega para a rota clicada, removendo itens após ela do backstack
            navController.navigate(route) {
                // Remove todas as rotas até a clicada
                popUpTo(route) {
                    inclusive = false
                }
                launchSingleTop = true
            }
        },
        modifier = modifier
    )
}

/**
 * Componente de Breadcrumbs reutilizável (versão manual)
 *
 * @param items Lista de itens do breadcrumb
 * @param onNavigate Callback para navegação ao clicar em um item
 * @param modifier Modificador opcional
 */
@Composable
fun Breadcrumbs(
    items: List<BreadcrumbItem>,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            // Ícone do item (se fornecido)
            if (item.icon != null) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (item.isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Texto do item
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (item.isActive) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = if (item.route != null && !item.isActive) {
                    Modifier.clickable { onNavigate(item.route) }
                } else {
                    Modifier
                }
            )

            // Separador (não adiciona depois do último item)
            if (index < items.size - 1) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Separador",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Função auxiliar para obter ícone baseado no nome da rota
 */
fun getIconForRoute(route: String): ImageVector? {
    return when {
        route.contains("dashboard") -> Icons.Default.Dashboard
        route.contains("task") -> Icons.Default.Task
        route.contains("project") -> Icons.Default.Folder
        route.contains("employee") || route.contains("user") -> Icons.Default.Person
        route.contains("team") -> Icons.Default.Groups
        route.contains("document") -> Icons.Default.Description
        else -> null
    }
}

/**
 * Função auxiliar para obter label traduzido baseado no nome da rota
 */
fun getLabelForRoute(route: String): String {
    return when {
        route.contains("dashboard") -> "Dashboard"
        route.contains("task_detail") -> "Detalhes da Tarefa"
        route.contains("tasks") -> "Tarefas"
        route.contains("project_detail") -> "Detalhes do Projeto"
        route.contains("project_create") -> "Criar Projeto"
        route.contains("project_edit") -> "Editar Projeto"
        route.contains("projects") -> "Projetos"
        route.contains("employee_detail") -> "Detalhes do Colaborador"
        route.contains("employee_create") -> "Criar Colaborador"
        route.contains("employee_edit") -> "Editar Colaborador"
        route.contains("users") -> "Colaboradores"
        route.contains("team_detail") -> "Detalhes do Time"
        route.contains("team_create") -> "Criar Time"
        route.contains("team_edit") -> "Editar Time"
        route.contains("teams") -> "Times"
        route.contains("document_detail") -> "Detalhes do Documento"
        route.contains("document_create") -> "Criar Documento"
        route.contains("document_edit") -> "Editar Documento"
        route.contains("documents") -> "Documentos"
        else -> route
    }
}

/**
 * Constrói uma lista de breadcrumb items baseado na rota atual
 * Exemplo: "project_detail/123" -> [Dashboard, Projetos, Detalhes do Projeto]
 */
fun buildBreadcrumbsFromRoute(currentRoute: String): List<BreadcrumbItem> {
    val items = mutableListOf<BreadcrumbItem>()

    // Sempre começa com Dashboard
    items.add(
        BreadcrumbItem(
            label = "Dashboard",
            icon = Icons.Default.Dashboard,
            route = "dashboard",
            isActive = currentRoute == "dashboard"
        )
    )

    // Se não está no dashboard, adiciona os níveis intermediários
    if (currentRoute != "dashboard") {
        when {
            // TAREFAS
            currentRoute.contains("task_detail") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Tarefas",
                        icon = Icons.Default.Task,
                        route = "tasks",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Detalhes da Tarefa",
                        icon = null,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("tasks") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Tarefas",
                        icon = Icons.Default.Task,
                        route = null,
                        isActive = true
                    )
                )
            }

            // PROJETOS
            currentRoute.contains("project_detail") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Projetos",
                        icon = Icons.Default.Folder,
                        route = "projects/NONE",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Detalhes do Projeto",
                        icon = null,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("project_create") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Projetos",
                        icon = Icons.Default.Folder,
                        route = "projects/NONE",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Criar Projeto",
                        icon = Icons.Default.Add,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("project_edit") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Projetos",
                        icon = Icons.Default.Folder,
                        route = "projects/NONE",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Editar Projeto",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("projects") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Projetos",
                        icon = Icons.Default.Folder,
                        route = null,
                        isActive = true
                    )
                )
            }

            // COLABORADORES
            currentRoute.contains("employee_detail") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Colaboradores",
                        icon = Icons.Default.Person,
                        route = "users",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Detalhes do Colaborador",
                        icon = null,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("employee_create") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Colaboradores",
                        icon = Icons.Default.Person,
                        route = "users",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Criar Colaborador",
                        icon = Icons.Default.Add,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("employee_edit") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Colaboradores",
                        icon = Icons.Default.Person,
                        route = "users",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Editar Colaborador",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("users") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Colaboradores",
                        icon = Icons.Default.Person,
                        route = null,
                        isActive = true
                    )
                )
            }

            // TIMES
            currentRoute.contains("team_detail") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Times",
                        icon = Icons.Default.Groups,
                        route = "teams",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Detalhes do Time",
                        icon = null,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("team_create") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Times",
                        icon = Icons.Default.Groups,
                        route = "teams",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Criar Time",
                        icon = Icons.Default.Add,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("team_edit") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Times",
                        icon = Icons.Default.Groups,
                        route = "teams",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Editar Time",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("teams") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Times",
                        icon = Icons.Default.Groups,
                        route = null,
                        isActive = true
                    )
                )
            }

            // DOCUMENTOS
            currentRoute.contains("document_detail") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Documentos",
                        icon = Icons.Default.Description,
                        route = "documents",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Detalhes do Documento",
                        icon = null,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("document_create") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Documentos",
                        icon = Icons.Default.Description,
                        route = "documents",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Criar Documento",
                        icon = Icons.Default.Add,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("document_edit") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Documentos",
                        icon = Icons.Default.Description,
                        route = "documents",
                        isActive = false
                    )
                )
                items.add(
                    BreadcrumbItem(
                        label = "Editar Documento",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }
            currentRoute.contains("documents") -> {
                items.add(
                    BreadcrumbItem(
                        label = "Documentos",
                        icon = Icons.Default.Description,
                        route = null,
                        isActive = true
                    )
                )
            }
        }
    }

    return items
}
