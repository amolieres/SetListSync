package com.amolieres.setlistync.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class MainViewModel() : ViewModel() {

    private val _Event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> = _Event


    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    fun onScreenEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.OnSettingsClicked -> _uiState.update { it.copy(showSettingsDialog = true) }
            MainUiEvent.OnSettingsDismiss -> _uiState.update { it.copy(showSettingsDialog = false) }
        }
    }

    private fun emit(event: MainEvent) {
        viewModelScope.launch { _Event.emit(event) }
    }
}
