package com.amolieres.setlistync.feature.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.usecase.CreateBandUseCase
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
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class MainViewModel(
    observeAllBands: ObserveAllBandsUseCase,
    private val createBand: CreateBandUseCase,
) : ViewModel() {

    private val _event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> = _event

    private data class ViewState(
        val showSettingsDialog: Boolean = false,
        val showCreateBandDialog: Boolean = false,
        val createBandName: String = ""
    )

    private val _viewState = MutableStateFlow(ViewState())

    val uiState: StateFlow<MainUiState> = combine(
        observeAllBands(),
        _viewState
    ) { bands, view ->
        MainUiState(
            isLoading = false,
            bands = bands,
            showSettingsDialog = view.showSettingsDialog,
            showCreateBandDialog = view.showCreateBandDialog,
            createBandName = view.createBandName
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MainUiState())

    fun onScreenEvent(event: MainUiEvent) {
        when (event) {
            MainUiEvent.OnSettingsClicked -> _viewState.update { it.copy(showSettingsDialog = true) }
            MainUiEvent.OnSettingsDismiss -> _viewState.update { it.copy(showSettingsDialog = false) }
            MainUiEvent.OnCreateBandClicked -> _viewState.update {
                it.copy(showCreateBandDialog = true, createBandName = "")
            }
            MainUiEvent.OnCreateBandDismiss -> _viewState.update {
                it.copy(showCreateBandDialog = false, createBandName = "")
            }
            is MainUiEvent.OnCreateBandNameChanged -> _viewState.update { it.copy(createBandName = event.name) }
            MainUiEvent.OnCreateBandConfirmed -> createNewBand()
            is MainUiEvent.OnBandClicked -> emit(MainEvent.NavigateToBandDetail(event.bandId))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun createNewBand() {
        val name = _viewState.value.createBandName.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            createBand(Band(id = Uuid.random().toString(), name = name))
            _viewState.update { it.copy(showCreateBandDialog = false, createBandName = "") }
            // No manual reload needed — Room Flow re-emits automatically
        }
    }

    private fun emit(event: MainEvent) {
        viewModelScope.launch { _event.emit(event) }
    }
}
