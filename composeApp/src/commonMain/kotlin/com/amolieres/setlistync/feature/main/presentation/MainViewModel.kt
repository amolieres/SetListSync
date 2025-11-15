package com.amolieres.setlistync.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainViewModel() : ViewModel() {

    private val _uiEvent = MutableSharedFlow<MainUiEvent>()
    val uiEvent: SharedFlow<MainUiEvent> = _uiEvent


    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    fun onScreenEvent(event: MainEvent) {
        when (event) {
            MainEvent.OnLogoutClicked -> {
                _uiState.value = _uiState.value.copy(
                    showConfirmDialog = ConfirmDialogType.Logout
                )
            }
            MainEvent.OnDeleteAccountClicked -> {
                _uiState.value = _uiState.value.copy(
                    showConfirmDialog = ConfirmDialogType.DeleteAccount
                )
            }
            MainEvent.OnLogoutConfirmed -> logout()
            MainEvent.OnDeleteConfirmed -> deleteAccount()
            MainEvent.OnDialogDismiss -> hideDialog()
        }
    }

    private fun hideDialog() {
        _uiState.value = _uiState.value.copy(showConfirmDialog = null)
    }
    private fun logout() {
        hideDialog()
        viewModelScope.launch {
            emit(MainUiEvent.NavigateToLogin)
        }
    }

    private fun deleteAccount() {
        hideDialog()
        viewModelScope.launch {
            emit(MainUiEvent.NavigateToLogin)
        }
    }

    private fun emit(event: MainUiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}
