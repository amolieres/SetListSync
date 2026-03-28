package com.amolieres.setlistync.feature.band.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.DeleteBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BandDetailViewModel(
    savedStateHandle: SavedStateHandle,
    observeBand: ObserveBandUseCase,
    private val deleteBand: DeleteBandUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _event = MutableSharedFlow<BandDetailEvent>()
    val event: SharedFlow<BandDetailEvent> = _event.asSharedFlow()

    private val _showDeleteBandConfirm = MutableStateFlow(false)

    val uiState: StateFlow<BandDetailUiState> = combine(
        observeBand(bandId),
        _showDeleteBandConfirm
    ) { band, showConfirm ->
        BandDetailUiState(
            isLoading = false,
            band = band,
            showDeleteBandConfirm = showConfirm
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandDetailUiState())

    fun onScreenEvent(event: BandDetailUiEvent) {
        when (event) {
            BandDetailUiEvent.OnEditInfoClicked ->
                viewModelScope.launch { _event.emit(BandDetailEvent.NavigateToEdit) }
            BandDetailUiEvent.OnMembersSectionClicked ->
                viewModelScope.launch { _event.emit(BandDetailEvent.NavigateToMembers) }
            BandDetailUiEvent.OnDeleteBandClicked ->
                _showDeleteBandConfirm.update { true }
            BandDetailUiEvent.OnDeleteBandConfirmed -> doDeleteBand()
            BandDetailUiEvent.OnDeleteBandDismiss ->
                _showDeleteBandConfirm.update { false }
        }
    }

    private fun doDeleteBand() {
        viewModelScope.launch {
            deleteBand(bandId)
            _event.emit(BandDetailEvent.NavigateBack)
        }
    }
}
