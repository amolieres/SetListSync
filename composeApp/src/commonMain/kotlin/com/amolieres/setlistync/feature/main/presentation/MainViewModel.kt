package com.amolieres.setlistync.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.ObserveAllBandsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    observeAllBands: ObserveAllBandsUseCase,
) : ViewModel() {

    private val _event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> = _event

    private data class ViewState(
        val showSettingsDialog: Boolean = false
    )

    private val _viewState = MutableStateFlow(ViewState())

    val uiState: StateFlow<MainUiState> = combine(
        observeAllBands(),
        _viewState
    ) { bands, view ->
        MainUiState(
            isLoading = false,
            bands = bands,
            showSettingsDialog = view.showSettingsDialog
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun onScreenEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.OnSettingsClicked -> _viewState.update { it.copy(showSettingsDialog = true) }
            MainUiEvent.OnSettingsDismiss -> _viewState.update { it.copy(showSettingsDialog = false) }
            MainUiEvent.OnCreateBandClicked -> emit(MainEvent.NavigateToBandCreation)
            is MainUiEvent.OnBandClicked -> emit(MainEvent.NavigateToBandDetail(event.bandId))
        }
    }

    private fun emit(event: MainEvent) {
        viewModelScope.launch { _event.emit(event) }
    }
}
