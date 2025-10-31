package com.project.taskmanagercivil.presentation.screens.settings

import com.project.taskmanagercivil.data.MockData
import com.project.taskmanagercivil.domain.models.Role
import com.project.taskmanagercivil.domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserManagementUiState(
    val users: List<User> = emptyList(),
    val selectedRoleFilter: Role? = null,
    val filteredUsers: List<User> = emptyList()
)

class UserManagementViewModel {
    private val _uiState = MutableStateFlow(UserManagementUiState())
    val uiState: StateFlow<UserManagementUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        val mockData = MockData()
        _uiState.update { currentState ->
            currentState.copy(
                users = mockData.users,
                filteredUsers = mockData.users
            )
        }
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

    fun saveUser(user: User) {
        _uiState.update { currentState ->
            val updatedUsers = if (currentState.users.any { it.id == user.id }) {
                // Editar usuário existente
                currentState.users.map {
                    if (it.id == user.id) user else it
                }
            } else {
                // Adicionar novo usuário
                currentState.users + user
            }

            val filtered = filterUsers(updatedUsers, currentState.selectedRoleFilter)

            currentState.copy(
                users = updatedUsers,
                filteredUsers = filtered
            )
        }
    }

    fun toggleUserActive(user: User) {
        val updatedUser = user.copy(isActive = !user.isActive)
        saveUser(updatedUser)
    }

    fun setUserActive(user: User, isActive: Boolean) {
        val updatedUser = user.copy(isActive = isActive)
        saveUser(updatedUser)
    }

    fun updateUserRoles(userId: String, newRoles: List<Role>) {
        _uiState.update { currentState ->
            val updatedUsers = currentState.users.map { user ->
                if (user.id == userId) {
                    user.copy(roles = newRoles)
                } else {
                    user
                }
            }

            val filtered = filterUsers(updatedUsers, currentState.selectedRoleFilter)

            currentState.copy(
                users = updatedUsers,
                filteredUsers = filtered
            )
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
