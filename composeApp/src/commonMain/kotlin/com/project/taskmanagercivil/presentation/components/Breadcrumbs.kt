package com.project.taskmanagercivil.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * @param currentRoot Raiz contextual da navegação (dashboard, projects, tasks, users, teams, documents)
 * @param modifier Modificador opcional
 */
@Composable
fun DynamicBreadcrumbs(
    navController: NavController,
    currentRoot: String = "dashboard",
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    // Constrói lista de breadcrumbs baseado na rota atual e raiz contextual
    val breadcrumbItems = remember(currentRoute, currentRoot) {
        buildBreadcrumbsFromRoute(currentRoute, currentRoot)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEachIndexed { index, item ->
            // Badge com o texto do breadcrumb
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (item.isActive) {
                    MaterialTheme.colorScheme.secondaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = if (item.route != null && !item.isActive) {
                    Modifier.clickable { onNavigate(item.route) }
                } else {
                    Modifier
                }
            ) {
                Text(
                    text = item.label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (item.isActive) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

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
 * Constrói uma lista de breadcrumb items baseado na rota atual e raiz contextual
 * Exemplo com root="projects": "project_detail/123" -> [Projetos, Detalhes do Projeto]
 * Exemplo com root="dashboard": "project_detail/123" -> [Dashboard, Projetos, Detalhes do Projeto]
 */
fun buildBreadcrumbsFromRoute(currentRoute: String, currentRoot: String = "dashboard"): List<BreadcrumbItem> {
    val items = mutableListOf<BreadcrumbItem>()

    // Determina a raiz baseado no contexto ou na rota atual
    val rootRoute = when {
        currentRoute.contains("dashboard") -> "dashboard"
        currentRoute.contains("task") -> currentRoot.takeIf { it == "dashboard" } ?: "tasks"
        currentRoute.contains("project") -> currentRoot.takeIf { it == "dashboard" } ?: "projects"
        currentRoute.contains("employee") || currentRoute.contains("user") -> currentRoot.takeIf { it == "dashboard" } ?: "users"
        currentRoute.contains("team") -> currentRoot.takeIf { it == "dashboard" } ?: "teams"
        currentRoute.contains("document") -> currentRoot.takeIf { it == "dashboard" } ?: "documents"
        else -> currentRoot
    }

    // Adiciona a raiz apropriada
    when (rootRoute) {
        "dashboard" -> {
            items.add(
                BreadcrumbItem(
                    label = "Dashboard",
                    icon = Icons.Default.Dashboard,
                    route = "dashboard",
                    isActive = currentRoute == "dashboard"
                )
            )
        }
        "projects" -> {
            items.add(
                BreadcrumbItem(
                    label = "Projetos",
                    icon = Icons.Default.Folder,
                    route = "projects/NONE",
                    isActive = currentRoute.contains("projects") && !currentRoute.contains("_")
                )
            )
        }
        "tasks" -> {
            items.add(
                BreadcrumbItem(
                    label = "Tarefas",
                    icon = Icons.Default.Task,
                    route = "tasks",
                    isActive = currentRoute == "tasks"
                )
            )
        }
        "users" -> {
            items.add(
                BreadcrumbItem(
                    label = "Colaboradores",
                    icon = Icons.Default.Person,
                    route = "users",
                    isActive = currentRoute == "users"
                )
            )
        }
        "teams" -> {
            items.add(
                BreadcrumbItem(
                    label = "Times",
                    icon = Icons.Default.Groups,
                    route = "teams",
                    isActive = currentRoute == "teams"
                )
            )
        }
        "documents" -> {
            items.add(
                BreadcrumbItem(
                    label = "Documentos",
                    icon = Icons.Default.Description,
                    route = "documents",
                    isActive = currentRoute == "documents"
                )
            )
        }
    }

    // Se não está na raiz, adiciona os níveis intermediários
    val isOnRootScreen = currentRoute == rootRoute ||
                         (rootRoute == "projects" && currentRoute.contains("projects") && !currentRoute.contains("_")) ||
                         (rootRoute == "tasks" && currentRoute == "tasks") ||
                         (rootRoute == "users" && currentRoute == "users") ||
                         (rootRoute == "teams" && currentRoute == "teams") ||
                         (rootRoute == "documents" && currentRoute == "documents") ||
                         (rootRoute == "dashboard" && currentRoute == "dashboard")

    if (!isOnRootScreen) {
        when {
            // TAREFAS
            currentRoute.contains("task_detail") -> {
                // Só adiciona "Tarefas" se não for a raiz atual
                if (rootRoute != "tasks") {
                    items.add(
                        BreadcrumbItem(
                            label = "Tarefas",
                            icon = Icons.Default.Task,
                            route = "tasks",
                            isActive = false
                        )
                    )
                }
                items.add(
                    BreadcrumbItem(
                        label = "Detalhes da Tarefa",
                        icon = null,
                        route = null,
                        isActive = true
                    )
                )
            }

            // PROJETOS
            currentRoute.contains("project_detail") -> {
                // Só adiciona "Projetos" se não for a raiz atual
                if (rootRoute != "projects") {
                    items.add(
                        BreadcrumbItem(
                            label = "Projetos",
                            icon = Icons.Default.Folder,
                            route = "projects/NONE",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Projetos" se não for a raiz atual
                if (rootRoute != "projects") {
                    items.add(
                        BreadcrumbItem(
                            label = "Projetos",
                            icon = Icons.Default.Folder,
                            route = "projects/NONE",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Projetos" se não for a raiz atual
                if (rootRoute != "projects") {
                    items.add(
                        BreadcrumbItem(
                            label = "Projetos",
                            icon = Icons.Default.Folder,
                            route = "projects/NONE",
                            isActive = false
                        )
                    )
                }
                items.add(
                    BreadcrumbItem(
                        label = "Editar Projeto",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }

            // COLABORADORES
            currentRoute.contains("employee_detail") -> {
                // Só adiciona "Colaboradores" se não for a raiz atual
                if (rootRoute != "users") {
                    items.add(
                        BreadcrumbItem(
                            label = "Colaboradores",
                            icon = Icons.Default.Person,
                            route = "users",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Colaboradores" se não for a raiz atual
                if (rootRoute != "users") {
                    items.add(
                        BreadcrumbItem(
                            label = "Colaboradores",
                            icon = Icons.Default.Person,
                            route = "users",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Colaboradores" se não for a raiz atual
                if (rootRoute != "users") {
                    items.add(
                        BreadcrumbItem(
                            label = "Colaboradores",
                            icon = Icons.Default.Person,
                            route = "users",
                            isActive = false
                        )
                    )
                }
                items.add(
                    BreadcrumbItem(
                        label = "Editar Colaborador",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }

            // TIMES
            currentRoute.contains("team_detail") -> {
                // Só adiciona "Times" se não for a raiz atual
                if (rootRoute != "teams") {
                    items.add(
                        BreadcrumbItem(
                            label = "Times",
                            icon = Icons.Default.Groups,
                            route = "teams",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Times" se não for a raiz atual
                if (rootRoute != "teams") {
                    items.add(
                        BreadcrumbItem(
                            label = "Times",
                            icon = Icons.Default.Groups,
                            route = "teams",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Times" se não for a raiz atual
                if (rootRoute != "teams") {
                    items.add(
                        BreadcrumbItem(
                            label = "Times",
                            icon = Icons.Default.Groups,
                            route = "teams",
                            isActive = false
                        )
                    )
                }
                items.add(
                    BreadcrumbItem(
                        label = "Editar Time",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }

            // DOCUMENTOS
            currentRoute.contains("document_detail") -> {
                // Só adiciona "Documentos" se não for a raiz atual
                if (rootRoute != "documents") {
                    items.add(
                        BreadcrumbItem(
                            label = "Documentos",
                            icon = Icons.Default.Description,
                            route = "documents",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Documentos" se não for a raiz atual
                if (rootRoute != "documents") {
                    items.add(
                        BreadcrumbItem(
                            label = "Documentos",
                            icon = Icons.Default.Description,
                            route = "documents",
                            isActive = false
                        )
                    )
                }
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
                // Só adiciona "Documentos" se não for a raiz atual
                if (rootRoute != "documents") {
                    items.add(
                        BreadcrumbItem(
                            label = "Documentos",
                            icon = Icons.Default.Description,
                            route = "documents",
                            isActive = false
                        )
                    )
                }
                items.add(
                    BreadcrumbItem(
                        label = "Editar Documento",
                        icon = Icons.Default.Edit,
                        route = null,
                        isActive = true
                    )
                )
            }
        }
    }

    return items
}
