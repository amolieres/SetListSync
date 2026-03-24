package com.amolieres.setlistync.feature.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.usecase.CreateUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LoginUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

class UserAuthViewModel(
    private val createUserUseCase: CreateUserUseCase,
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserAuthUiState())
    val uiState: StateFlow<UserAuthUiState> = _uiState

    private val _event = MutableSharedFlow<UserAuthEvent>()
    val event: SharedFlow<UserAuthEvent> = _event.asSharedFlow()

    fun onScreenEvent(event: UserAuthUiEvent) {
        when (event) {
            is UserAuthUiEvent.FirstNameChanged -> _uiState.update { it.copy(firstName = event.value, error = null) }
            is UserAuthUiEvent.LastNameChanged -> _uiState.update { it.copy(lastName = event.value, error = null) }
            is UserAuthUiEvent.EmailChanged -> _uiState.update { it.copy(email = event.value, error = null) }
            is UserAuthUiEvent.PasswordChanged -> _uiState.update { it.copy(password = event.value, error = null) }
            is UserAuthUiEvent.ToggleMode -> toggleMode()
            is UserAuthUiEvent.Submit -> submit()
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
                _uiState.update { it.copy(isLoading = true, error = null) }
                if (state.isLoginMode) {
                    handleLogin(state.email, state.password)
                } else {
                    handleAccountCreation(state)
                }.onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }.onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, currentUser = user) }
                    _event.emit(UserAuthEvent.OnSubmitSuccess(user))
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    private suspend fun handleLogin(email: String, password: String) : Result<User> =
        loginUserUseCase.invoke(email, password)

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun handleAccountCreation(state: UserAuthUiState): Result<User> =
        createUserUseCase(
            state.firstName,
            state.lastName,
            state.email,
            state.password
        )

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }
}