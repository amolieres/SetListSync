package com.amolieres.setlistync.feature.band.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.DeleteBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.song.usecase.ObserveSongsUseCase
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
    private val deleteBand: DeleteBandUseCase,
    observeSongs: ObserveSongsUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _event = MutableSharedFlow<BandDetailEvent>()
    val event: SharedFlow<BandDetailEvent> = _event.asSharedFlow()

    private data class ViewState(
        val showDeleteBandConfirm: Boolean = false
    )

    private val _viewState = MutableStateFlow(ViewState())

    val uiState: StateFlow<BandDetailUiState> = combine(
        observeBand(bandId),
        observeSongs(bandId),
        _viewState
    ) { band, songs, view ->
        BandDetailUiState(
            isLoading = false,
            band = band,
            showDeleteBandConfirm = view.showDeleteBandConfirm,
            songCount = songs.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandDetailUiState())

    fun onScreenEvent(event: BandDetailUiEvent) {
        when (event) {
            BandDetailUiEvent.OnEditInfoClicked ->
                viewModelScope.launch { _event.emit(BandDetailEvent.NavigateToEdit) }
            BandDetailUiEvent.OnMembersSectionClicked ->
                viewModelScope.launch { _event.emit(BandDetailEvent.NavigateToMembers) }
            BandDetailUiEvent.OnSongsSectionClicked ->
                viewModelScope.launch { _event.emit(BandDetailEvent.NavigateToSongs) }
            BandDetailUiEvent.OnGigsSectionClicked ->
                viewModelScope.launch { _event.emit(BandDetailEvent.NavigateToGigs) }
            BandDetailUiEvent.OnDeleteBandClicked ->
                _viewState.update { it.copy(showDeleteBandConfirm = true) }
            BandDetailUiEvent.OnDeleteBandConfirmed -> doDeleteBand()
            BandDetailUiEvent.OnDeleteBandDismiss ->
                _viewState.update { it.copy(showDeleteBandConfirm = false) }
        }
    }

    private fun doDeleteBand() {
        viewModelScope.launch {
            deleteBand(bandId)
            _event.emit(BandDetailEvent.NavigateBack)
        }
    }
}
