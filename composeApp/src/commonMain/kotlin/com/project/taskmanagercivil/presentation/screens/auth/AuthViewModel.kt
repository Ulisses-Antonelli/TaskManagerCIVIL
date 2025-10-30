package com.project.taskmanagercivil.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.taskmanagercivil.domain.models.User
import com.project.taskmanagercivil.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar autenticação
 */
class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthentication()
    }

    private fun checkAuthentication() {
        viewModelScope.launch {
            val isAuthenticated = authRepository.isAuthenticated()
            if (isAuthenticated) {
                val user = authRepository.getCurrentUser()
                _uiState.update {
                    it.copy(
                        currentUser = user,
                        isAuthenticated = true
                    )
                }
            }
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    fun onPasswordVisibilityToggle() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // Validações
        if (email.isEmpty()) {
            _uiState.update { it.copy(error = "Por favor, insira seu email") }
            return
        }

        if (!email.contains("@")) {
            _uiState.update { it.copy(error = "Email inválido") }
            return
        }

        if (password.isEmpty()) {
            _uiState.update { it.copy(error = "Por favor, insira sua senha") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.login(email, password)

            result.onSuccess { user ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        currentUser = user,
                        error = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = exception.message ?: "Erro ao fazer login"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                AuthUiState() // Reset para estado inicial
            }
        }
    }

    // Recuperação de senha
    fun onRecoveryEmailChange(email: String) {
        _uiState.update { it.copy(recoveryEmail = email, recoveryError = null) }
    }

    fun sendPasswordResetEmail() {
        val email = _uiState.value.recoveryEmail.trim()

        if (email.isEmpty()) {
            _uiState.update { it.copy(recoveryError = "Por favor, insira seu email") }
            return
        }

        if (!email.contains("@")) {
            _uiState.update { it.copy(recoveryError = "Email inválido") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRecoveryLoading = true, recoveryError = null) }

            val result = authRepository.sendPasswordResetEmail(email)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isRecoveryLoading = false,
                        recoverySuccess = true,
                        recoveryError = null
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isRecoveryLoading = false,
                        recoveryError = exception.message ?: "Erro ao enviar email"
                    )
                }
            }
        }
    }

    fun resetRecoveryState() {
        _uiState.update {
            it.copy(
                recoveryEmail = "",
                recoveryError = null,
                recoverySuccess = false,
                isRecoveryLoading = false
            )
        }
    }
}

/**
 * Estado da UI de autenticação
 */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null,

    // Recuperação de senha
    val recoveryEmail: String = "",
    val isRecoveryLoading: Boolean = false,
    val recoverySuccess: Boolean = false,
    val recoveryError: String? = null
)
