package com.amolieres.setlistync.feature.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.preferences.SetNotationUseCase
import com.amolieres.setlistync.core.domain.user.usecase.DeleteCurrentUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.GetCurrentUserUseCase
import com.amolieres.setlistync.core.domain.user.usecase.LogoutUserUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SettingsViewModel(
    private val getCurrentUser: GetCurrentUserUseCase,
    private val observeNotation: ObserveNotationUseCase,
    private val setNotation: SetNotationUseCase,
    private val logoutUser: LogoutUserUseCase,
    private val deleteCurrentUser: DeleteCurrentUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                observeNotation(),
                flow { emit(getCurrentUser()) }
            ) { notation, user ->
                SettingsUiState(
                    isLoading = false,
                    userName = user?.firstName ?: "",
                    userEmail =  user?.email  ?: "",
                    notation = notation
                )
            }.collect { _uiState.value = it }
        }
    }

    fun onScreenEvent(event: SettingsUiEvent) {
        when (event) {
            is SettingsUiEvent.NotationChanged ->
                viewModelScope.launch { setNotation(event.notation) }
            SettingsUiEvent.OnDeleteAccountClicked -> {
                _uiState.update {
                    it.copy(showConfirmDialog = ConfirmDialogType.DeleteAccount)
                }
            }
            SettingsUiEvent.OnDeleteConfirmed -> confirmDeleteAccount()
            SettingsUiEvent.OnDialogDismiss -> hideDialog()
            SettingsUiEvent.OnLogoutClicked -> {
                _uiState.update { it.copy(showConfirmDialog = ConfirmDialogType.Logout) }
            }
            SettingsUiEvent.OnLogoutConfirmed -> confirmLogout()
        }
    }

    private fun hideDialog() {
        _uiState.update { it.copy(showConfirmDialog = null) }
    }

    fun confirmLogout() {
        hideDialog()
        viewModelScope.launch {
            logoutUser()
            _events.emit(SettingsEvent.NavigateToLogin)
        }
    }

    fun confirmDeleteAccount() {
        hideDialog()
        viewModelScope.launch {
            deleteCurrentUser()
            _events.emit(SettingsEvent.NavigateToLogin)
        }
    }
}
