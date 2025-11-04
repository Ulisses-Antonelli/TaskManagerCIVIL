package com.project.taskmanagercivil.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.project.taskmanagercivil.presentation.ViewModelFactory
import com.project.taskmanagercivil.presentation.screens.auth.ForgotPasswordScreen
import com.project.taskmanagercivil.presentation.screens.auth.LoginScreen
import com.project.taskmanagercivil.presentation.screens.dashboard.DashboardScreenContent
import com.project.taskmanagercivil.presentation.screens.documents.DocumentDetailScreen
import com.project.taskmanagercivil.presentation.screens.documents.DocumentFormScreen
import com.project.taskmanagercivil.presentation.screens.documents.DocumentsScreenContent
import com.project.taskmanagercivil.presentation.screens.employees.EmployeeDetailScreen
import com.project.taskmanagercivil.presentation.screens.employees.EmployeeFormScreen
import com.project.taskmanagercivil.presentation.screens.employees.EmployeesScreenContent
import com.project.taskmanagercivil.presentation.screens.projects.ProjectDetailScreen
import com.project.taskmanagercivil.presentation.screens.projects.ProjectsScreenContent
import com.project.taskmanagercivil.presentation.screens.tasks.TasksScreenContent
import com.project.taskmanagercivil.presentation.screens.settings.SettingsScreenContent
import com.project.taskmanagercivil.presentation.screens.settings.UserManagementScreen
import com.project.taskmanagercivil.presentation.screens.teams.TeamDetailScreen
import com.project.taskmanagercivil.presentation.screens.teams.TeamFormScreen
import com.project.taskmanagercivil.presentation.screens.teams.TeamsScreenContent
import com.project.taskmanagercivil.presentation.screens.financial.FinancialScreenContent


/**
 * Objeto singleton para compartilhar estado de navegação entre telas
 * Necessário porque o Navigation Compose para Web não expõe os path parameters facilmente
 */
object NavigationState {
    var pendingProjectFilter: String? = null
    var pendingProjectId: String? = null
    var pendingTaskId: String? = null
    var pendingEmployeeId: String? = null
    var pendingTeamId: String? = null
    var currentRoot: String = "dashboard" // Raiz atual da navegação (dashboard, projects, tasks, users, teams, documents)
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object ForgotPassword : Screen("forgot_password")
    object Dashboard : Screen("dashboard")
    object Tasks : Screen("tasks")
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    object Projects : Screen("projects/{statusFilter}") {
        val baseRoute = "projects"
        // Rota com filtro: projects/TODO ou projects/IN_PROGRESS
        fun createRoute(statusFilter: String? = null) =
            if (statusFilter != null) "projects/$statusFilter" else "projects/NONE"
    }
    object ProjectDetail : Screen("project_detail/{projectId}") {
        fun createRoute(projectId: String) = "project_detail/$projectId"
    }
    object ProjectCreate : Screen("project_create")
    object ProjectEdit : Screen("project_edit/{projectId}") {
        fun createRoute(projectId: String) = "project_edit/$projectId"
    }
    object Employees : Screen("users")
    object EmployeeDetail : Screen("employee_detail/{employeeId}") {
        fun createRoute(employeeId: String) = "employee_detail/$employeeId"
    }
    object EmployeeCreate : Screen("employee_create")
    object EmployeeEdit : Screen("employee_edit/{employeeId}") {
        fun createRoute(employeeId: String) = "employee_edit/$employeeId"
    }
    object Teams : Screen("teams")
    object TeamDetail : Screen("team_detail/{teamId}") {
        fun createRoute(teamId: String) = "team_detail/$teamId"
    }
    object TeamCreate : Screen("team_create")
    object TeamEdit : Screen("team_edit/{teamId}") {
        fun createRoute(teamId: String) = "team_edit/$teamId"
    }
    object Documents : Screen("documents")
    object DocumentDetail : Screen("document_detail/{documentId}") {
        fun createRoute(documentId: String) = "document_detail/$documentId"
    }
    object DocumentCreate : Screen("document_create")
    object DocumentEdit : Screen("document_edit/{documentId}") {
        fun createRoute(documentId: String) = "document_edit/$documentId"
    }
    object UserManagement : Screen("user_management")
    object Financial : Screen("financial")
    object FinancialTasks : Screen("financial/tasks")
    object FinancialProjects : Screen("financial/projects")
    object FinancialCompany : Screen("financial/company")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    // Sincroniza navegação com URL do navegador (apenas no Wasm)
    BrowserNavigationSync(navController)

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {

        // Tela de Login
        composable(Screen.Login.route) {
            val viewModel = ViewModelFactory.createAuthViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        // Tela de Recuperação de Senha
        composable(Screen.ForgotPassword.route) {
            val viewModel = ViewModelFactory.createAuthViewModel()
            ForgotPasswordScreen(
                viewModel = viewModel,
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Tela de Dashboard
        composable(Screen.Dashboard.route) {
            NavigationState.currentRoot = "dashboard"
            val viewModel = ViewModelFactory.createDashboardViewModel()
            DashboardScreenContent(
                navController = navController,
                viewModel = viewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onProjectClick = { projectId ->
                    NavigationState.pendingProjectId = projectId
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onTaskClick = { taskId ->
                    NavigationState.pendingTaskId = taskId
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onProjectsWithStatusClick = { status ->
                    // Guarda o filtro pendente no singleton antes de navegar
                    NavigationState.pendingProjectFilter = status.name
                    // Navega para tela de projetos com filtro de status
                    navController.navigate(Screen.Projects.createRoute(status.name))
                }
            )
        }

        composable(Screen.Tasks.route) {
            NavigationState.currentRoot = "tasks"
            val viewModel = ViewModelFactory.createTasksViewModel()
            TasksScreenContent(
                navController = navController,
                viewModel = viewModel,
                onTaskClick = { task ->
                    NavigationState.pendingTaskId = task.id
                    navController.navigate(Screen.TaskDetail.createRoute(task.id))
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tela de Detalhes da Tarefa
        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Usa o singleton para obter o taskId, similar à solução do projectId
            val taskId = androidx.compose.runtime.remember {
                NavigationState.pendingTaskId?.also {
                    NavigationState.pendingTaskId = null
                } ?: "1"
            }

            val viewModel = androidx.compose.runtime.remember(taskId) {
                ViewModelFactory.createTaskDetailViewModel(taskId)
            }

            com.project.taskmanagercivil.presentation.screens.tasks.TaskDetailScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { taskId ->
                    // TODO: Implementar tela de edição de tarefa quando necessário
                },
                onDelete = { taskId ->
                    // TODO: Implementar deleteTask no TaskRepository quando necessário
                    navController.popBackStack()
                },
                onProjectClick = { projectId ->
                    NavigationState.pendingProjectId = projectId
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onEmployeeClick = { employeeId ->
                    NavigationState.pendingEmployeeId = employeeId
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onRelatedTaskClick = { relatedTaskId ->
                    NavigationState.pendingTaskId = relatedTaskId
                    navController.navigate(Screen.TaskDetail.createRoute(relatedTaskId))
                }
            )
        }


        // Rota Projects - aceita filtro opcional via path parameter
        composable(
            route = Screen.Projects.route,
            arguments = listOf(
                navArgument("statusFilter") {
                    type = NavType.StringType
                    defaultValue = "NONE"
                    nullable = false
                }
            )
        ) { backStackEntry ->
            // IMPORTANTE: Usa remember para manter a mesma instância do ViewModel
            val viewModel = androidx.compose.runtime.remember {
                ViewModelFactory.createProjectsViewModel()
            }

            // Extrai o statusFilter do path parameter analisando a URL atual
            // Ex: se navegou para "projects/TODO", extrai "TODO"
            val currentDestination = navController.currentBackStackEntry?.destination
            val currentRoute = currentDestination?.route ?: ""

            // Pega todas as entries do backstack e procura pela rota atual
            val allEntries = navController.currentBackStack.value
            val currentEntry = allEntries.lastOrNull()
            val actualRoute = currentEntry?.destination?.route ?: ""

            println("DEBUG - Current route template: $currentRoute")
            println("DEBUG - Actual route from backstack: $actualRoute")
            println("DEBUG - All backstack entries: ${allEntries.map { it.destination.route }}")

            // Como o navigation não expõe o valor real facilmente, vou precisar
            // interceptar na navegação e guardar em um estado global
            // Por enquanto, vou tentar pegar via ID da entrada
            val entryId = backStackEntry.id
            println("DEBUG - BackStack Entry ID: $entryId")

            // Solução temporária: vou armazenar o filtro em um objeto singleton
            // e aplicar aqui
            val filterToApply = NavigationState.pendingProjectFilter
            println("DEBUG - Pending filter from singleton: $filterToApply")

            androidx.compose.runtime.LaunchedEffect(filterToApply) {
                if (filterToApply != null && filterToApply != "NONE") {
                    try {
                        val status = com.project.taskmanagercivil.domain.models.TaskStatus.valueOf(filterToApply)
                        println("DEBUG - Aplicando filtro: $status")
                        viewModel.onTaskStatusFilterChange(status)
                        // Limpa o filtro pendente
                        NavigationState.pendingProjectFilter = null
                    } catch (e: Exception) {
                        println("DEBUG - Erro ao converter status: ${e.message}")
                    }
                }
            }

            NavigationState.currentRoot = "projects"
            ProjectsScreenContent(
                navController = navController,
                viewModel = viewModel,
                onProjectClick = { projectId ->
                    NavigationState.pendingProjectId = projectId
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onCreateProject = {
                    navController.navigate(Screen.ProjectCreate.route)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tela de Detalhes do Projeto
        composable(
            route = Screen.ProjectDetail.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Usa o singleton para obter o projectId, similar à solução do filtro de status
            val projectId = androidx.compose.runtime.remember {
                NavigationState.pendingProjectId?.also {
                    NavigationState.pendingProjectId = null
                } ?: "1"
            }

            val viewModel = androidx.compose.runtime.remember(projectId) {
                ViewModelFactory.createProjectDetailViewModel(projectId)
            }

            ProjectDetailScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { projectId ->
                    navController.navigate(Screen.ProjectEdit.createRoute(projectId))
                },
                onDelete = { projectId ->
                    // Deleta e volta para a lista
                    viewModel.deleteProject(projectId)
                    navController.popBackStack()
                },
                onTaskClick = { taskId ->
                    NavigationState.pendingTaskId = taskId
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onEmployeeClick = { employeeId ->
                    NavigationState.pendingEmployeeId = employeeId
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onTeamClick = { teamId ->
                    navController.navigate(Screen.TeamDetail.createRoute(teamId))
                }
            )
        }

        // Tela de Colaboradores
        composable(Screen.Employees.route) {
            NavigationState.currentRoot = "users"
            val viewModel = ViewModelFactory.createEmployeesViewModel()
            EmployeesScreenContent(
                navController = navController,
                viewModel = viewModel,
                onEmployeeClick = { employeeId ->
                    NavigationState.pendingEmployeeId = employeeId
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onCreateEmployee = {
                    navController.navigate(Screen.EmployeeCreate.route)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tela de Detalhes do Colaborador
        composable(
            route = Screen.EmployeeDetail.route,
            arguments = listOf(
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            NavigationState.currentRoot = "users"
            // Usa o singleton para obter o employeeId
            val employeeId = androidx.compose.runtime.remember {
                NavigationState.pendingEmployeeId?.also {
                    NavigationState.pendingEmployeeId = null
                } ?: "1"
            }
            println("DEBUG Navigation: Employee ID from route: '$employeeId'")

            val viewModel = ViewModelFactory.createEmployeeDetailViewModel(employeeId)
            EmployeeDetailScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { employeeId ->
                    navController.navigate(Screen.EmployeeEdit.createRoute(employeeId))
                },
                onDelete = { employeeId ->
                    viewModel.deleteEmployee(employeeId)
                    navController.popBackStack()
                },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }

        // Tela de Criar Colaborador
        composable(Screen.EmployeeCreate.route) {
            val viewModel = ViewModelFactory.createEmployeeFormViewModel(null)
            EmployeeFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack(Screen.Employees.route, inclusive = false)
                }
            )
        }

        // Tela de Editar Colaborador
        composable(
            route = Screen.EmployeeEdit.route,
            arguments = listOf(
                navArgument("employeeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val employeeId = currentRoute.removePrefix("employee_edit/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createEmployeeFormViewModel(employeeId)
            EmployeeFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Tela de Times
        composable(Screen.Teams.route) {
            NavigationState.currentRoot = "teams"
            val viewModel = ViewModelFactory.createTeamsViewModel()
            TeamsScreenContent(
                navController = navController,
                viewModel = viewModel,
                onTeamClick = { teamId ->
                    NavigationState.pendingTeamId = teamId
                    navController.navigate(Screen.TeamDetail.createRoute(teamId))
                },
                onCreateTeam = {
                    navController.navigate(Screen.TeamCreate.route)
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tela de Detalhes do Time
        composable(
            route = Screen.TeamDetail.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            NavigationState.currentRoot = "teams"
            // Usa o singleton para obter o teamId
            val teamId = androidx.compose.runtime.remember {
                NavigationState.pendingTeamId?.also {
                    NavigationState.pendingTeamId = null
                } ?: "1"
            }

            val viewModel = androidx.compose.runtime.remember(teamId) {
                ViewModelFactory.createTeamDetailViewModel(teamId)
            }

            TeamDetailScreen(
                navController = navController,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { teamId ->
                    navController.navigate(Screen.TeamEdit.createRoute(teamId))
                },
                onDelete = { teamId ->
                    viewModel.deleteTeam(teamId)
                    navController.popBackStack()
                },
                onEmployeeClick = { employeeId ->
                    NavigationState.pendingEmployeeId = employeeId
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onProjectClick = { projectId ->
                    NavigationState.pendingProjectId = projectId
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onTaskClick = { taskId ->
                    NavigationState.pendingTaskId = taskId
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        // Tela de Criar Time
        composable(Screen.TeamCreate.route) {
            val viewModel = ViewModelFactory.createTeamFormViewModel(null)
            TeamFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack(Screen.Teams.route, inclusive = false)
                }
            )
        }

        // Tela de Editar Time
        composable(
            route = Screen.TeamEdit.route,
            arguments = listOf(
                navArgument("teamId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val teamId = currentRoute.removePrefix("team_edit/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createTeamFormViewModel(teamId)
            TeamFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Tela de Documentos
        composable(Screen.Documents.route) {
            val viewModel = ViewModelFactory.createDocumentsViewModel()
            DocumentsScreenContent(
                navController = navController,
                viewModel = viewModel,
                onDocumentClick = { documentId ->
                    navController.navigate(Screen.DocumentDetail.createRoute(documentId))
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tela de Detalhes do Documento
        composable(
            route = Screen.DocumentDetail.route,
            arguments = listOf(
                navArgument("documentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val documentId = currentRoute.removePrefix("document_detail/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createDocumentDetailViewModel(documentId)
            DocumentDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { documentId ->
                    navController.navigate(Screen.DocumentEdit.createRoute(documentId))
                },
                onDelete = { documentId ->
                    viewModel.deleteDocument(documentId)
                    navController.popBackStack()
                },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                }
            )
        }

        // Tela de Criar Documento
        composable(Screen.DocumentCreate.route) {
            val viewModel = ViewModelFactory.createDocumentFormViewModel(null)
            DocumentFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack(Screen.Documents.route, inclusive = false)
                }
            )
        }

        // Tela de Editar Documento
        composable(
            route = Screen.DocumentEdit.route,
            arguments = listOf(
                navArgument("documentId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val documentId = currentRoute.removePrefix("document_edit/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createDocumentFormViewModel(documentId)
            DocumentFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Tela de Financeiro (apenas para ADMIN e GESTOR_OBRAS)
        composable(Screen.Financial.route) {
            NavigationState.currentRoot = "financial"
            val financialViewModel = remember { ViewModelFactory.createFinancialViewModel() }
            FinancialScreenContent(
                navController = navController,
                viewModel = financialViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        // Tela de Gerenciamento de Usuários (apenas para ADMIN)
        composable(Screen.UserManagement.route) {
            // Obtém o usuário autenticado do AuthViewModel global
            val authViewModel = ViewModelFactory.getAuthViewModel()
            val authState by authViewModel.uiState.collectAsState()

            UserManagementScreen(
                navController = navController,
                currentUser = authState.currentUser
            )
        }

        // Tela de Configurações
        composable(Screen.Settings.route) {
            NavigationState.currentRoot = "settings"
            SettingsScreenContent(
                navController = navController,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
