package com.project.taskmanagercivil.presentation

import com.project.taskmanagercivil.data.repository.DashboardRepositoryImpl
import com.project.taskmanagercivil.data.repository.DocumentRepositoryImpl
import com.project.taskmanagercivil.data.repository.EmployeeRepositoryImpl
import com.project.taskmanagercivil.data.repository.ProjectRepositoryImpl
import com.project.taskmanagercivil.data.repository.TaskRepositoryImpl
import com.project.taskmanagercivil.data.repository.TeamRepositoryImpl
import com.project.taskmanagercivil.domain.repository.DashboardRepository
import com.project.taskmanagercivil.domain.repository.DocumentRepository
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.ProjectRepository
import com.project.taskmanagercivil.domain.repository.TaskRepository
import com.project.taskmanagercivil.domain.repository.TeamRepository
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
import com.project.taskmanagercivil.presentation.screens.tasks.TasksViewModel
import com.project.taskmanagercivil.presentation.screens.teams.TeamDetailViewModel
import com.project.taskmanagercivil.presentation.screens.teams.TeamFormViewModel
import com.project.taskmanagercivil.presentation.screens.teams.TeamsViewModel

/**
 * Factory simples para criação de ViewModels com injeção de dependência por construtor
 */
object ViewModelFactory {
    private val dashboardRepository: DashboardRepository = DashboardRepositoryImpl()
    private val taskRepository: TaskRepository = TaskRepositoryImpl()
    private val projectRepository: ProjectRepository = ProjectRepositoryImpl()
    private val employeeRepository: EmployeeRepository = EmployeeRepositoryImpl()
    private val teamRepository: TeamRepository = TeamRepositoryImpl()
    private val documentRepository: DocumentRepository = DocumentRepositoryImpl()

    fun createDashboardViewModel(): DashboardViewModel {
        return DashboardViewModel(dashboardRepository)
    }

    fun createTasksViewModel(): TasksViewModel {
        return TasksViewModel(taskRepository)
    }

    fun createProjectsViewModel(): ProjectsViewModel {
        return ProjectsViewModel(projectRepository, taskRepository)
    }

    fun createProjectDetailViewModel(projectId: String): ProjectDetailViewModel {
        return ProjectDetailViewModel(projectRepository, projectId)
    }

    fun createProjectFormViewModel(projectId: String? = null): ProjectFormViewModel {
        return ProjectFormViewModel(projectRepository, projectId)
    }

    fun createEmployeesViewModel(): EmployeesViewModel {
        return EmployeesViewModel(employeeRepository)
    }

    fun createEmployeeDetailViewModel(employeeId: String): EmployeeDetailViewModel {
        return EmployeeDetailViewModel(employeeId, employeeRepository, projectRepository)
    }

    fun createEmployeeFormViewModel(employeeId: String? = null): EmployeeFormViewModel {
        return EmployeeFormViewModel(employeeId, employeeRepository, projectRepository)
    }

    fun createTeamsViewModel(): TeamsViewModel {
        return TeamsViewModel(teamRepository)
    }

    fun createTeamDetailViewModel(teamId: String): TeamDetailViewModel {
        return TeamDetailViewModel(teamId, teamRepository, employeeRepository, projectRepository)
    }

    fun createTeamFormViewModel(teamId: String? = null): TeamFormViewModel {
        return TeamFormViewModel(teamId, teamRepository, employeeRepository, projectRepository)
    }

    fun createDocumentsViewModel(): DocumentsViewModel {
        return DocumentsViewModel(documentRepository)
    }

    fun createDocumentDetailViewModel(documentId: String): DocumentDetailViewModel {
        return DocumentDetailViewModel(documentId, documentRepository, projectRepository, employeeRepository)
    }

    fun createDocumentFormViewModel(documentId: String? = null): DocumentFormViewModel {
        return DocumentFormViewModel(documentId, documentRepository, projectRepository, employeeRepository)
    }
}
