package com.project.taskmanagercivil.presentation.screens.settings

import com.project.taskmanagercivil.data.repository.UserRepositoryImpl
import com.project.taskmanagercivil.domain.models.Employee
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.domain.repository.EmployeeRepository
import com.project.taskmanagercivil.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val selectedRoleFilter: Role? = null,
    val filteredUsers: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class UserManagementViewModel(
    private val userRepository: UserRepository,
    private val employeeRepository: EmployeeRepository,
    private val viewModelScope: CoroutineScope
) {
    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
        observeUsers()
    }

    /**
     * Observa mudanças nos usuários do repositório
     */
    private fun observeUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _uiState.update { currentState ->
                    val filtered = filterUsers(users, currentState.selectedRoleFilter)
                    currentState.copy(
                        users = users,
                        filteredUsers = filtered,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Carrega usuários do backend
     */
    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                (userRepository as? UserRepositoryImpl)?.loadUsers()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erro ao carregar usuários: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Recarrega usuários do backend
     */
    fun refreshUsers() {
        loadUsers()
    }

    fun setRoleFilter(role: Role?) {
        _uiState.update { currentState ->
            val filtered = if (role == null) {
                currentState.users
            } else {
                currentState.users.filter { it.hasRole(role) }
            }
            currentState.copy(
                selectedRoleFilter = role,
                filteredUsers = filtered
            )
        }
    }

    /**
     * Salva usuário (cria novo ou atualiza existente)
     */
    fun saveUser(user: User) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val isNewUser = !_uiState.value.users.any { it.id == user.id }

                if (isNewUser) {
                    // Criar novo usuário no backend
                    (userRepository as? UserRepositoryImpl)?.createUser(
                        username = user.username,
                        email = user.email,
                        password = "senha123", // TODO: Solicitar senha no formulário
                        fullName = user.name,
                        roles = user.roles.map { "ROLE_${it.name}" },
                        isActive = user.isActive
                    )

                    // Criar Employee correspondente
                    val newEmployee = Employee(
                        id = user.id,
                        fullName = user.name,
                        role = "A definir",
                        email = user.email,
                        phone = null,
                        cpf = null,
                        hireDate = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                        terminationDate = null,
                        avatarUrl = null,
                        projectIds = emptyList(),
                        isActive = user.isActive
                    )
                    employeeRepository.addEmployee(newEmployee)
                } else {
                    // Atualizar usuário existente no backend
                    (userRepository as? UserRepositoryImpl)?.updateUser(
                        id = user.id,
                        email = user.email,
                        fullName = user.name,
                        isActive = user.isActive
                    )
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erro ao salvar usuário: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Alterna status ativo/inativo do usuário
     */
    fun toggleUserActive(user: User) {
        viewModelScope.launch {
            try {
                val repo = userRepository as? UserRepositoryImpl
                if (user.isActive) {
                    repo?.deactivateUser(user.id)
                } else {
                    repo?.activateUser(user.id)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao alterar status: ${e.message}") }
            }
        }
    }

    /**
     * Define status ativo do usuário
     */
    fun setUserActive(user: User, isActive: Boolean) {
        viewModelScope.launch {
            try {
                val repo = userRepository as? UserRepositoryImpl
                if (isActive) {
                    repo?.activateUser(user.id)
                } else {
                    repo?.deactivateUser(user.id)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao alterar status: ${e.message}") }
            }
        }
    }

    /**
     * Atualiza roles do usuário
     */
    fun updateUserRoles(userId: String, newRoles: List<Role>) {
        viewModelScope.launch {
            try {
                val rolesStrings = newRoles.map { "ROLE_${it.name}" }
                (userRepository as? UserRepositoryImpl)?.updateUserRoles(userId, rolesStrings)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao atualizar roles: ${e.message}") }
            }
        }
    }

    /**
     * Remove usuário
     */
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                (userRepository as? UserRepositoryImpl)?.deleteUser(userId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Erro ao remover usuário: ${e.message}") }
            }
        }
    }

    private fun filterUsers(users: List<User>, roleFilter: Role?): List<User> {
        return if (roleFilter == null) {
            users
        } else {
            users.filter { it.hasRole(roleFilter) }
        }
    }
}
