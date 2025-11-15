package com.amolieres.setlistync.feature.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.usecase.CreateUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetUserByEmailUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserAuthViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserByEmailUseCase: GetUserByEmailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserAuthUiState())
    val uiState: StateFlow<UserAuthUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UserAuthUiEvent>()
    val uiEvent: SharedFlow<UserAuthUiEvent> = _uiEvent.asSharedFlow()

    fun onScreenEvent(event: UserAuthEvent) {
        when (event) {
            is UserAuthEvent.FirstNameChanged -> _uiState.update { it.copy(firstName = event.value, error = null) }
            is UserAuthEvent.LastNameChanged -> _uiState.update { it.copy(lastName = event.value, error = null) }
            is UserAuthEvent.EmailChanged -> _uiState.update { it.copy(email = event.value, error = null) }
            is UserAuthEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value, error = null) }
            is UserAuthEvent.ToggleMode -> toggleMode()
            is UserAuthEvent.Submit -> submit()
        }
    }

    private fun toggleMode() {
        _uiState.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                error = null
            )
        }
    }

    private fun submit() {
        val state = _uiState.value
        if (!isValidEmail(state.email)) {
            _uiState.update { it.copy(error = "Please enter a valid email address.") }
            return
        }

        if (!state.isFormValid) {
            _uiState.update { it.copy(error = "Please fill all required fields.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                if (state.isLoginMode) {
                    handleLogin(state.email, state.password)
                } else {
                    handleAccountCreation(state)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private suspend fun handleLogin(email: String, password: String) {
        val existingUser = getUserByEmailUseCase(email)
        if (existingUser == null) {
            _uiState.update {
                it.copy(isLoading = false, error = "User not found. Please create an account.")
            }
        } else if (existingUser.passwordHash != password) {
            _uiState.update {
                it.copy(isLoading = false, error = "Incorrect password.")
            }
        } else {
            _uiState.update { it.copy(isLoading = false, currentUser = existingUser) }
            _uiEvent.emit(UserAuthUiEvent.OnSubmitSuccess(existingUser))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun handleAccountCreation(state: UserAuthUiState) {
        val existingUser = getUserByEmailUseCase(state.email)
        if (existingUser != null) {
            _uiState.update {
                it.copy(isLoading = false, error = "This email is already registered.")
            }
            return
        }

        val newUser = User(
            id = Uuid.random().toString(),
            firstName = state.firstName.trim(),
            lastName = state.lastName.trim(),
            email = state.email.trim(),
            passwordHash = state.password
        )

        createUserUseCase(newUser)
        _uiState.update { it.copy(isLoading = false, currentUser = newUser) }
        _uiEvent.emit(UserAuthUiEvent.OnSubmitSuccess(newUser))
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }
}