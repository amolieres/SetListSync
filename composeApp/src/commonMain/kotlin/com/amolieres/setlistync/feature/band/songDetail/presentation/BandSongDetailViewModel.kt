package com.amolieres.setlistync.feature.band.songDetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongKey
import com.amolieres.setlistync.core.domain.song.model.SongSearchResult
import com.amolieres.setlistync.core.domain.song.usecase.AddSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.GetSongAudioFeaturesUseCase
import com.amolieres.setlistync.core.domain.song.usecase.GetSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.SearchSongsUseCase
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
    private val updateSong: UpdateSongUseCase,
    private val searchSongs: SearchSongsUseCase,
    private val getAudioFeatures: GetSongAudioFeaturesUseCase,
    observeNotation: ObserveNotationUseCase
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
        val key: SongKey? = null,
        val tempo: String = "",
        val originalArtist: String = "",
        val isSaving: Boolean = false,
        val originalSong: Song? = null,
        val searchQuery: String = "",
        val isSearching: Boolean = false,
        val searchResults: List<SongSearchResult> = emptyList(),
        val isLoadingFeatures: Boolean = false,
        val noteNotation: NoteNotation = NoteNotation.EN
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
                originalArtist = view.originalArtist,
                isSaving = view.isSaving,
                searchQuery = view.searchQuery,
                isSearching = view.isSearching,
                searchResults = view.searchResults,
                isLoadingFeatures = view.isLoadingFeatures,
                noteNotation = view.noteNotation
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            BandSongDetailUiState(isEditMode = isEditMode)
        )

    init {
        viewModelScope.launch {
            observeNotation().collect { notation ->
                _viewState.update { it.copy(noteNotation = notation) }
            }
        }
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
                            key = song.key,
                            tempo = song.tempo?.toString() ?: "",
                            originalArtist = song.originalArtist ?: "",
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
            is BandSongDetailUiEvent.OnOriginalArtistChanged ->
                _viewState.update { it.copy(originalArtist = event.artist) }
            BandSongDetailUiEvent.OnSaveClicked -> save()
            BandSongDetailUiEvent.OnBackClicked ->
                viewModelScope.launch { _event.emit(BandSongDetailEvent.NavigateBack) }
            is BandSongDetailUiEvent.OnSearchQueryChanged -> {
                println("[BandSongDetailVM] OnSearchQueryChanged — \"${event.query}\"")
                _viewState.update { it.copy(searchQuery = event.query) }
            }
            BandSongDetailUiEvent.OnSearchSubmitted -> {
                println("[BandSongDetailVM] OnSearchSubmitted")
                submitSearch()
            }
            is BandSongDetailUiEvent.OnSearchResultSelected -> selectResult(event.result)
            BandSongDetailUiEvent.OnSearchResultsDismissed ->
                _viewState.update { it.copy(searchResults = emptyList()) }
        }
    }

    private fun submitSearch() {
        val query = _viewState.value.searchQuery.trim()
        println("[BandSongDetailVM] submitSearch — query=\"$query\" (blank=${query.isBlank()})")
        if (query.isBlank()) return
        viewModelScope.launch {
            _viewState.update { it.copy(isSearching = true, searchResults = emptyList()) }
            searchSongs(query)
                .onSuccess { results ->
                    println("[BandSongDetailVM] submitSearch — success, ${results.size} result(s)")
                    _viewState.update { it.copy(isSearching = false, searchResults = results) }
                }
                .onFailure { error ->
                    println("[BandSongDetailVM] submitSearch — failure: ${error::class.simpleName}: ${error.message}")
                    _viewState.update { it.copy(isSearching = false) }
                }
        }
    }

    private fun selectResult(result: SongSearchResult) {
        println("[BandSongDetailVM] selectResult — \"${result.title}\" by ${result.artist}")
        val mins = result.durationSeconds / 60
        val secs = result.durationSeconds % 60
        _viewState.update {
            it.copy(
                searchResults = emptyList(),
                searchQuery = "",
                title = result.title,
                originalArtist = result.artist,
                minutes = if (mins > 0) mins.toString() else "",
                seconds = if (secs > 0) secs.toString() else "",
                isLoadingFeatures = true
            )
        }
        viewModelScope.launch {
            getAudioFeatures(result.title, result.artist)
                .onSuccess { (bpm, keyString) ->
                    val songKey = keyString?.let { SongKey.fromEnglishName(it) }
                    _viewState.update {
                        it.copy(
                            isLoadingFeatures = false,
                            tempo = bpm?.toString() ?: it.tempo,
                            key = songKey ?: it.key
                        )
                    }
                }
                .onFailure {
                    _viewState.update { it.copy(isLoadingFeatures = false) }
                }
        }
    }

    private fun save() {
        val view = _viewState.value
        val title = view.title.trim()
        if (title.isBlank()) return
        val minutes = view.minutes.toIntOrNull() ?: 0
        val seconds = view.seconds.toIntOrNull() ?: 0
        val durationSeconds = minutes * 60 + seconds
        val key = view.key
        val tempo = view.tempo.toIntOrNull()
        val originalArtist = view.originalArtist.trim().takeIf { it.isNotBlank() }

        _viewState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            if (isEditMode) {
                val original = view.originalSong ?: return@launch
                val updated = original.copy(
                    title = title,
                    durationSeconds = durationSeconds,
                    key = key,
                    tempo = tempo,
                    originalArtist = originalArtist
                )
                runCatching { updateSong(bandId, updated) }
                    .onSuccess { _event.emit(BandSongDetailEvent.NavigateBack) }
                    .onFailure { _viewState.update { it.copy(isSaving = false) } }
            } else {
                addSong(bandId, title, durationSeconds, key, tempo, originalArtist = originalArtist)
                    .onSuccess { _event.emit(BandSongDetailEvent.NavigateBack) }
                    .onFailure { _viewState.update { it.copy(isSaving = false) } }
            }
        }
    }
}
