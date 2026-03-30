package com.amolieres.setlistync.feature.band.songs.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.usecase.AddSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.DeleteSongUseCase
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

class BandSongsViewModel(
    savedStateHandle: SavedStateHandle,
    observeBand: ObserveBandUseCase,
    observeSongs: ObserveSongsUseCase,
    private val addSong: AddSongUseCase,
    private val deleteSong: DeleteSongUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _event = MutableSharedFlow<BandSongsEvent>()
    val event: SharedFlow<BandSongsEvent> = _event.asSharedFlow()

    private data class ViewState(
        val showSongDialog: Boolean = false,
        val songTitle: String = "",
        val songMinutes: String = "",
        val songSeconds: String = "",
        val songKey: String = "",
        val songTempo: String = "",
        val isSavingSong: Boolean = false
    )

    private val _viewState = MutableStateFlow(ViewState())

    val uiState: StateFlow<BandSongsUiState> = combine(
        observeBand(bandId),
        observeSongs(bandId),
        _viewState
    ) { band, songs, view ->
        BandSongsUiState(
            isLoading = false,
            bandName = band?.name ?: "",
            songs = songs,
            showSongDialog = view.showSongDialog,
            songTitle = view.songTitle,
            songMinutes = view.songMinutes,
            songSeconds = view.songSeconds,
            songKey = view.songKey,
            songTempo = view.songTempo,
            isSavingSong = view.isSavingSong
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandSongsUiState())

    fun onScreenEvent(event: BandSongsUiEvent) {
        when (event) {
            BandSongsUiEvent.OnAddSongClicked ->
                _viewState.update { it.copy(showSongDialog = true) }
            is BandSongsUiEvent.OnSongTitleChanged ->
                _viewState.update { it.copy(songTitle = event.title) }
            is BandSongsUiEvent.OnSongMinutesChanged ->
                _viewState.update { it.copy(songMinutes = event.minutes) }
            is BandSongsUiEvent.OnSongSecondsChanged ->
                _viewState.update { it.copy(songSeconds = event.seconds) }
            is BandSongsUiEvent.OnSongKeyChanged ->
                _viewState.update { it.copy(songKey = event.key) }
            is BandSongsUiEvent.OnSongTempoChanged ->
                _viewState.update { it.copy(songTempo = event.tempo) }
            BandSongsUiEvent.OnSongDialogConfirmed -> saveNewSong()
            BandSongsUiEvent.OnSongDialogDismiss -> dismissSongDialog()
            is BandSongsUiEvent.OnDeleteSongClicked -> doDeleteSong(event.songId)
        }
    }

    private fun saveNewSong() {
        val view = _viewState.value
        val title = view.songTitle.trim()
        if (title.isBlank()) return
        val minutes = view.songMinutes.toIntOrNull() ?: 0
        val seconds = view.songSeconds.toIntOrNull() ?: 0
        val durationSeconds = minutes * 60 + seconds
        val key = view.songKey.trim().takeIf { it.isNotBlank() }
        val tempo = view.songTempo.toIntOrNull()

        _viewState.update { it.copy(isSavingSong = true) }
        viewModelScope.launch {
            addSong(bandId, title, durationSeconds, key, tempo)
                .onSuccess {
                    _viewState.update {
                        it.copy(
                            isSavingSong = false,
                            showSongDialog = false,
                            songTitle = "", songMinutes = "", songSeconds = "",
                            songKey = "", songTempo = ""
                        )
                    }
                }
                .onFailure {
                    _viewState.update { it.copy(isSavingSong = false) }
                }
        }
    }

    private fun dismissSongDialog() {
        _viewState.update {
            it.copy(
                showSongDialog = false,
                songTitle = "", songMinutes = "", songSeconds = "",
                songKey = "", songTempo = ""
            )
        }
    }

    private fun doDeleteSong(songId: SongId) {
        viewModelScope.launch {
            deleteSong(bandId, songId)
        }
    }
}
