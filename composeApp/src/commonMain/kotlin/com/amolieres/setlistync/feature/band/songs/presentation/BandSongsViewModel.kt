package com.amolieres.setlistync.feature.band.songs.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.usecase.DeleteSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.ObserveSongsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BandSongsViewModel(
    savedStateHandle: SavedStateHandle,
    observeBand: ObserveBandUseCase,
    observeSongs: ObserveSongsUseCase,
    private val deleteSong: DeleteSongUseCase,
    observeNotation: ObserveNotationUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _event = MutableSharedFlow<BandSongsEvent>()
    val event: SharedFlow<BandSongsEvent> = _event.asSharedFlow()

    val uiState: StateFlow<BandSongsUiState> = combine(
        observeBand(bandId),
        observeSongs(bandId),
        observeNotation()
    ) { band, songs, notation ->
        BandSongsUiState(
            isLoading = false,
            bandName = band?.name ?: "",
            songs = songs,
            noteNotation = notation
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandSongsUiState())

    fun onScreenEvent(event: BandSongsUiEvent) {
        when (event) {
            BandSongsUiEvent.OnAddSongClicked ->
                viewModelScope.launch { _event.emit(BandSongsEvent.NavigateToNewSong) }
            is BandSongsUiEvent.OnEditSongClicked ->
                viewModelScope.launch { _event.emit(BandSongsEvent.NavigateToEditSong(event.songId)) }
            is BandSongsUiEvent.OnDeleteSongClicked -> doDeleteSong(event.songId)
        }
    }

    private fun doDeleteSong(songId: SongId) {
        viewModelScope.launch {
            deleteSong(bandId, songId)
        }
    }
}
