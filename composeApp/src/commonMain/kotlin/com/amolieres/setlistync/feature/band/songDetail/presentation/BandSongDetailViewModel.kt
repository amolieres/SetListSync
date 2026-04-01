package com.amolieres.setlistync.feature.band.songDetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.usecase.AddSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.GetSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.UpdateSongUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BandSongDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getSong: GetSongUseCase,
    private val addSong: AddSongUseCase,
    private val updateSong: UpdateSongUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))
    private val songId: String? = savedStateHandle.get<String>("songId")
    val isEditMode: Boolean = songId != null

    private val _event = MutableSharedFlow<BandSongDetailEvent>()
    val event: SharedFlow<BandSongDetailEvent> = _event.asSharedFlow()

    private data class ViewState(
        val isLoading: Boolean = false,
        val title: String = "",
        val minutes: String = "",
        val seconds: String = "",
        val key: String = "",
        val tempo: String = "",
        val isSaving: Boolean = false,
        val originalSong: Song? = null
    )

    private val _viewState = MutableStateFlow(ViewState())

    val uiState: StateFlow<BandSongDetailUiState> = _viewState
        .map { view ->
            BandSongDetailUiState(
                isEditMode = isEditMode,
                isLoading = view.isLoading,
                title = view.title,
                minutes = view.minutes,
                seconds = view.seconds,
                key = view.key,
                tempo = view.tempo,
                isSaving = view.isSaving
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            BandSongDetailUiState(isEditMode = isEditMode)
        )

    init {
        if (songId != null) {
            _viewState.update { it.copy(isLoading = true) }
            viewModelScope.launch {
                val song = getSong(SongId(songId))
                if (song != null) {
                    val mins = song.durationSeconds / 60
                    val secs = song.durationSeconds % 60
                    _viewState.update {
                        it.copy(
                            isLoading = false,
                            title = song.title,
                            minutes = if (mins > 0) mins.toString() else "",
                            seconds = if (secs > 0) secs.toString() else "",
                            key = song.key ?: "",
                            tempo = song.tempo?.toString() ?: "",
                            originalSong = song
                        )
                    }
                } else {
                    _viewState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onScreenEvent(event: BandSongDetailUiEvent) {
        when (event) {
            is BandSongDetailUiEvent.OnTitleChanged ->
                _viewState.update { it.copy(title = event.title) }
            is BandSongDetailUiEvent.OnMinutesChanged ->
                _viewState.update { it.copy(minutes = event.minutes) }
            is BandSongDetailUiEvent.OnSecondsChanged ->
                _viewState.update { it.copy(seconds = event.seconds) }
            is BandSongDetailUiEvent.OnKeyChanged ->
                _viewState.update { it.copy(key = event.key) }
            is BandSongDetailUiEvent.OnTempoChanged ->
                _viewState.update { it.copy(tempo = event.tempo) }
            BandSongDetailUiEvent.OnSaveClicked -> save()
            BandSongDetailUiEvent.OnBackClicked ->
                viewModelScope.launch { _event.emit(BandSongDetailEvent.NavigateBack) }
        }
    }

    private fun save() {
        val view = _viewState.value
        val title = view.title.trim()
        if (title.isBlank()) return
        val minutes = view.minutes.toIntOrNull() ?: 0
        val seconds = view.seconds.toIntOrNull() ?: 0
        val durationSeconds = minutes * 60 + seconds
        val key = view.key.trim().takeIf { it.isNotBlank() }
        val tempo = view.tempo.toIntOrNull()

        _viewState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            if (isEditMode) {
                val original = view.originalSong ?: return@launch
                val updated = original.copy(
                    title = title,
                    durationSeconds = durationSeconds,
                    key = key,
                    tempo = tempo
                )
                runCatching { updateSong(bandId, updated) }
                    .onSuccess { _event.emit(BandSongDetailEvent.NavigateBack) }
                    .onFailure { _viewState.update { it.copy(isSaving = false) } }
            } else {
                addSong(bandId, title, durationSeconds, key, tempo)
                    .onSuccess { _event.emit(BandSongDetailEvent.NavigateBack) }
                    .onFailure { _viewState.update { it.copy(isSaving = false) } }
            }
        }
    }
}
