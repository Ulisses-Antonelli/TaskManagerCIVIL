package com.project.taskmanagercivil.presentation

import com.project.taskmanagercivil.data.repositories.AuthRepositoryImpl
import com.project.taskmanagercivil.data.repository.DashboardRepositoryImpl
import com.project.taskmanagercivil.data.repository.DocumentRepositoryImpl
import com.project.taskmanagercivil.data.repository.EmployeeRepositoryImpl
import com.project.taskmanagercivil.data.repository.FinancialRepositoryImpl
import com.project.taskmanagercivil.data.repository.ProjectRepositoryImpl
import com.project.taskmanagercivil.data.repository.TaskRepositoryImpl
import com.project.taskmanagercivil.data.repository.TeamRepositoryImpl
import com.project.taskmanagercivil.data.repository.ThemeRepositoryImpl
import com.project.taskmanagercivil.data.repository.UserRepositoryImpl
import com.project.taskmanagercivil.domain.repositories.AuthRepository
import com.project.taskmanagercivil.domain.repository.DashboardRepository
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.FinancialRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TaskRepository
import com.project.taskmanagercivil.domain.repository.TeamRepository
import com.project.taskmanagercivil.domain.repository.ThemeRepository
import com.project.taskmanagercivil.domain.repository.UserRepository
import com.project.taskmanagercivil.presentation.screens.auth.AuthViewModel
import com.project.taskmanagercivil.presentation.screens.dashboard.DashboardViewModel
import com.project.taskmanagercivil.presentation.screens.documents.DocumentDetailViewModel
import com.project.taskmanagercivil.presentation.screens.documents.DocumentFormViewModel
import com.project.taskmanagercivil.presentation.screens.documents.DocumentsViewModel
import com.project.taskmanagercivil.presentation.screens.employees.EmployeeDetailViewModel
import com.project.taskmanagercivil.presentation.screens.employees.EmployeeFormViewModel
import com.project.taskmanagercivil.presentation.screens.employees.EmployeesViewModel
import com.project.taskmanagercivil.presentation.screens.projects.ProjectDetailViewModel
import com.project.taskmanagercivil.presentation.screens.projects.ProjectFormViewModel
import com.project.taskmanagercivil.presentation.screens.projects.ProjectsViewModel
import com.project.taskmanagercivil.presentation.screens.tasks.TaskDetailViewModel
import com.project.taskmanagercivil.presentation.screens.tasks.TasksViewModel
import com.project.taskmanagercivil.presentation.screens.teams.TeamDetailViewModel
import com.project.taskmanagercivil.presentation.screens.teams.TeamFormViewModel
import com.project.taskmanagercivil.presentation.screens.teams.TeamsViewModel
import com.project.taskmanagercivil.presentation.screens.settings.UserManagementViewModel
import com.project.taskmanagercivil.presentation.screens.financial.FinancialViewModel
import com.project.taskmanagercivil.presentation.theme.ThemeViewModel

/**
 * Factory simples para criação de ViewModels com injeção de dependência por construtor
 */
object ViewModelFactory {
    private val authRepository: AuthRepository = AuthRepositoryImpl()
    private val dashboardRepository: DashboardRepository = DashboardRepositoryImpl()
    private val taskRepository: TaskRepository = TaskRepositoryImpl()
    private val projectRepository: ProjectRepository = ProjectRepositoryImpl()
    private val employeeRepository: EmployeeRepository = EmployeeRepositoryImpl()
    private val teamRepository: TeamRepository = TeamRepositoryImpl()
    private val documentRepository: DocumentRepository = DocumentRepositoryImpl()
    private val financialRepository: FinancialRepository = FinancialRepositoryImpl()
    private val userRepository: UserRepository = UserRepositoryImpl()
    private val themeRepository: ThemeRepository = ThemeRepositoryImpl()

    // Instância singleton do AuthViewModel (compartilhada globalmente)
    private var authViewModelInstance: AuthViewModel? = null

    // Instância singleton do ThemeViewModel (compartilhada globalmente)
    private var themeViewModelInstance: ThemeViewModel? = null

    fun createAuthViewModel(): AuthViewModel {
        if (authViewModelInstance == null) {
            authViewModelInstance = AuthViewModel(authRepository)
        }
        return authViewModelInstance!!
    }

    /**
     * Retorna o AuthViewModel singleton global
     * Usado para acessar o usuário logado em qualquer tela
     */
    fun getAuthViewModel(): AuthViewModel {
        return createAuthViewModel()
    }

    fun createDashboardViewModel(): DashboardViewModel {
        return DashboardViewModel(dashboardRepository)
    }

    fun createTasksViewModel(): TasksViewModel {
        return TasksViewModel(taskRepository, projectRepository, employeeRepository)
    }

    fun createTaskDetailViewModel(taskId: String): TaskDetailViewModel {
        return TaskDetailViewModel(taskId, taskRepository)
    }

    fun createProjectsViewModel(): ProjectsViewModel {
        return ProjectsViewModel(projectRepository, taskRepository)
    }

    fun createProjectDetailViewModel(projectId: String): ProjectDetailViewModel {
        return ProjectDetailViewModel(projectRepository, employeeRepository, teamRepository, projectId)
    }

    fun createProjectFormViewModel(projectId: String? = null): ProjectFormViewModel {
        return ProjectFormViewModel(projectRepository, projectId)
    }

    fun createEmployeesViewModel(): EmployeesViewModel {
        return EmployeesViewModel(employeeRepository, projectRepository, teamRepository)
    }

    fun createEmployeeDetailViewModel(employeeId: String): EmployeeDetailViewModel {
        return EmployeeDetailViewModel(employeeId, employeeRepository, projectRepository, taskRepository)
    }

    fun createEmployeeFormViewModel(employeeId: String? = null): EmployeeFormViewModel {
        return EmployeeFormViewModel(employeeId, employeeRepository, projectRepository)
    }

    fun createTeamsViewModel(): TeamsViewModel {
        return TeamsViewModel(teamRepository, employeeRepository, projectRepository)
    }

    fun createTeamDetailViewModel(teamId: String): TeamDetailViewModel {
        return TeamDetailViewModel(teamId, teamRepository, employeeRepository, projectRepository, taskRepository)
    }

    fun createTeamFormViewModel(teamId: String? = null): TeamFormViewModel {
        return TeamFormViewModel(teamId, teamRepository, employeeRepository, projectRepository)
    }

    fun createDocumentsViewModel(): DocumentsViewModel {
        return DocumentsViewModel(documentRepository, projectRepository, taskRepository)
    }

    fun createDocumentDetailViewModel(documentId: String): DocumentDetailViewModel {
        return DocumentDetailViewModel(documentId, documentRepository, projectRepository, employeeRepository)
    }

    fun createDocumentFormViewModel(documentId: String? = null): DocumentFormViewModel {
        return DocumentFormViewModel(documentId, documentRepository, projectRepository, employeeRepository)
    }

    fun createUserManagementViewModel(viewModelScope: kotlinx.coroutines.CoroutineScope): UserManagementViewModel {
        return UserManagementViewModel(userRepository, employeeRepository, viewModelScope)
    }

    fun createFinancialViewModel(): FinancialViewModel {
        return FinancialViewModel(financialRepository)
    }

    fun createThemeViewModel(): ThemeViewModel {
        if (themeViewModelInstance == null) {
            themeViewModelInstance = ThemeViewModel(themeRepository)
        }
        return themeViewModelInstance!!
    }

    /**
     * Retorna o ThemeViewModel singleton global
     * Usado para acessar e alterar o tema em qualquer tela
     */
    fun getThemeViewModel(): ThemeViewModel {
        return createThemeViewModel()
    }
}
