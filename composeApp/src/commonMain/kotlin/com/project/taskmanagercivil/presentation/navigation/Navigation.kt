package com.project.taskmanagercivil.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.project.taskmanagercivil.presentation.ViewModelFactory
import com.project.taskmanagercivil.presentation.screens.dashboard.DashboardScreenContent
import com.project.taskmanagercivil.presentation.screens.documents.DocumentDetailScreen
import com.project.taskmanagercivil.presentation.screens.documents.DocumentFormScreen
import com.project.taskmanagercivil.presentation.screens.documents.DocumentsScreenContent
import com.project.taskmanagercivil.presentation.screens.employees.EmployeeDetailScreen
import com.project.taskmanagercivil.presentation.screens.employees.EmployeeFormScreen
import com.project.taskmanagercivil.presentation.screens.employees.EmployeesScreenContent
import com.project.taskmanagercivil.presentation.screens.projects.ProjectDetailScreen
import com.project.taskmanagercivil.presentation.screens.projects.ProjectFormScreen
import com.project.taskmanagercivil.presentation.screens.projects.ProjectsScreenContent
import com.project.taskmanagercivil.presentation.screens.tasks.TasksScreenContent
import com.project.taskmanagercivil.presentation.screens.teams.TeamDetailScreen
import com.project.taskmanagercivil.presentation.screens.teams.TeamFormScreen
import com.project.taskmanagercivil.presentation.screens.teams.TeamsScreenContent


sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Tasks : Screen("tasks")
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
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {

        // Tela de Dashboard
        composable(Screen.Dashboard.route) {
            val viewModel = ViewModelFactory.createDashboardViewModel()
            DashboardScreenContent(
                viewModel = viewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
                },
                onTaskClick = { taskId ->
                    // TODO: Navegar para detalhes da tarefa quando implementado
                },
                onProjectsWithStatusClick = { status ->
                    // Navega para tela de projetos com filtro de status
                    navController.navigate(Screen.Projects.createRoute(status.name))
                }
            )
        }

        composable(Screen.Tasks.route) {
            val viewModel = ViewModelFactory.createTasksViewModel()
            TasksScreenContent(
                viewModel = viewModel,
                onTaskClick = { task ->
                    // TODO: Navegar para detalhes da tarefa quando implementado
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
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

            // Extrai o statusFilter do path parameter
            // Ex: "projects/TODO" -> "TODO"
            androidx.compose.runtime.LaunchedEffect(Unit) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
                val statusFilterParam = currentRoute.removePrefix("projects/").takeIf {
                    it.isNotEmpty() && it != "NONE" && it != "{statusFilter}"
                }

                println("DEBUG - Route completa: $currentRoute")
                println("DEBUG - statusFilter extraído: $statusFilterParam")

                if (statusFilterParam != null) {
                    try {
                        val status = com.project.taskmanagercivil.domain.models.TaskStatus.valueOf(statusFilterParam)
                        println("DEBUG - Aplicando filtro: $status")
                        // Quando vem do Dashboard, aplica filtro de tarefas internas
                        // (mostra todas as obras que possuem pelo menos uma tarefa com esse status)
                        viewModel.onTaskStatusFilterChange(status)
                    } catch (e: Exception) {
                        println("DEBUG - Erro ao converter status: ${e.message}")
                    }
                }
            }

            ProjectsScreenContent(
                viewModel = viewModel,
                onProjectClick = { projectId ->
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
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val projectId = currentRoute.removePrefix("project_detail/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createProjectDetailViewModel(projectId)
            ProjectDetailScreen(
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
                    // TODO: Navegar para detalhes da tarefa
                },
                onEmployeeClick = { employeeId ->
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onTeamClick = { teamId ->
                    navController.navigate(Screen.TeamDetail.createRoute(teamId))
                }
            )
        }

        // Tela de Criar Projeto
        composable(Screen.ProjectCreate.route) {
            val viewModel = ViewModelFactory.createProjectFormViewModel(null)
            ProjectFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack(Screen.Projects.route, inclusive = false)
                }
            )
        }

        // Tela de Editar Projeto
        composable(
            route = Screen.ProjectEdit.route,
            arguments = listOf(
                navArgument("projectId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val projectId = currentRoute.removePrefix("project_edit/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createProjectFormViewModel(projectId)
            ProjectFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Tela de Colaboradores
        composable(Screen.Employees.route) {
            val viewModel = ViewModelFactory.createEmployeesViewModel()
            EmployeesScreenContent(
                viewModel = viewModel,
                onEmployeeClick = { employeeId ->
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
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val employeeId = currentRoute.removePrefix("employee_detail/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createEmployeeDetailViewModel(employeeId)
            EmployeeDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEdit = { employeeId ->
                    navController.navigate(Screen.EmployeeEdit.createRoute(employeeId))
                },
                onDelete = { employeeId ->
                    viewModel.deleteEmployee(employeeId)
                    navController.popBackStack()
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
            val viewModel = ViewModelFactory.createTeamsViewModel()
            TeamsScreenContent(
                viewModel = viewModel,
                onTeamClick = { teamId ->
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
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            val teamId = currentRoute.removePrefix("team_detail/").takeIf { it.isNotBlank() } ?: "1"

            val viewModel = ViewModelFactory.createTeamDetailViewModel(teamId)
            TeamDetailScreen(
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
                    navController.navigate(Screen.EmployeeDetail.createRoute(employeeId))
                },
                onProjectClick = { projectId ->
                    navController.navigate(Screen.ProjectDetail.createRoute(projectId))
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
                viewModel = viewModel,
                onDocumentClick = { documentId ->
                    navController.navigate(Screen.DocumentDetail.createRoute(documentId))
                },
                onCreateDocument = {
                    navController.navigate(Screen.DocumentCreate.route)
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
    }
}
