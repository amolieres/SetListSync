package com.amolieres.setlistync.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.ObserveAllBandsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    observeAllBands: ObserveAllBandsUseCase,
) : ViewModel() {

    private val _event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> = _event

    val uiState: StateFlow<MainUiState> = observeAllBands()
        .map { bands -> MainUiState(isLoading = false, bands = bands) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun onScreenEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.OnSettingsClicked -> emit(MainEvent.NavigateToSettings)
            MainUiEvent.OnCreateBandClicked -> emit(MainEvent.NavigateToBandCreation)
            is MainUiEvent.OnBandClicked -> emit(MainEvent.NavigateToBandDetail(event.bandId))
        }
    }

    private fun emit(event: MainEvent) {
        viewModelScope.launch { _event.emit(event) }
    }
}
