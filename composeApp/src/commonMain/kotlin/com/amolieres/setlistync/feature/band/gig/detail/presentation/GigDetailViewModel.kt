package com.amolieres.setlistync.feature.band.gig.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.usecase.ComputeSetlistDurationUseCase
import com.amolieres.setlistync.core.domain.band.usecase.DeleteGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveGigsForBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateGigUseCase
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.usecase.ObserveSongsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GigDetailViewModel(
    savedStateHandle: SavedStateHandle,
    observeGigsForBand: ObserveGigsForBandUseCase,
    private val updateGig: UpdateGigUseCase,
    observeSongs: ObserveSongsUseCase,
    observeNotation: ObserveNotationUseCase,
    private val computeSetlistDuration: ComputeSetlistDurationUseCase,
    private val deleteGig: DeleteGigUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))
    val gigId: String = checkNotNull(savedStateHandle.get<String>("gigId"))
    private val initialIsEditing: Boolean = savedStateHandle.get<Boolean>("isEditing") ?: false

    private val _event = MutableSharedFlow<GigDetailEvent>()
    val event: SharedFlow<GigDetailEvent> = _event.asSharedFlow()

    private val _uiState = MutableStateFlow(GigDetailUiState(isEditing = initialIsEditing))
    val uiState: StateFlow<GigDetailUiState> = _uiState

    // Latest snapshot of all band songs — needed by refreshDerivedSongLists
    private var allSongs: List<Song> = emptyList()
    private var currentGig: Gig? = null

    // Ordered IDs received from the gig before songs have loaded
    private var pendingOrderedIds: List<SongId> = emptyList()

    init {
        // Observe the specific gig — every DB change flows through here and drives state updates
        viewModelScope.launch {
            observeGigsForBand(bandId).collect { gigs ->
                val gig = gigs.find { it.id == gigId }
                currentGig = gig
                if (gig != null) {
                    if (allSongs.isEmpty()) {
                        pendingOrderedIds = gig.orderedSongIds
                    } else {
                        refreshDerivedSongLists(gig.orderedSongIds)
                    }
                }
                _uiState.update { it.copy(isLoading = false, gig = gig) }
            }
        }

        // Observe band songs
        viewModelScope.launch {
            observeSongs(bandId).collect { songs ->
                allSongs = songs
                val orderedIds = if (pendingOrderedIds.isNotEmpty()) {
                    val ids = pendingOrderedIds
                    pendingOrderedIds = emptyList()
                    ids
                } else {
                    _uiState.value.setlistSongs.map { it.id }
                }
                refreshDerivedSongLists(orderedIds)
            }
        }

        // Observe notation preference
        viewModelScope.launch {
            observeNotation().collect { notation ->
                _uiState.update { it.copy(noteNotation = notation) }
            }
        }
    }

    /**
     * Sole entry point for rebuilding setlist-related state.
     * Called exclusively by the observables above — never directly by user actions.
     */
    private fun refreshDerivedSongLists(orderedIds: List<SongId>) {
        val songMap = allSongs.associateBy { it.id }
        val setlist = orderedIds.mapNotNull { songMap[it] }
        val setlistIds = setlist.map { it.id }.toSet()
        val catalog = allSongs.filter { it.id !in setlistIds }
        _uiState.update {
            it.copy(
                setlistSongs = setlist,
                catalogSongs = catalog,
                setlistDurationSeconds = computeSetlistDuration(setlist)
            )
        }
    }

    fun onScreenEvent(event: GigDetailUiEvent) {
        when (event) {
            GigDetailUiEvent.OnToggleEditing ->
                _uiState.update { it.copy(isEditing = !it.isEditing) }
            GigDetailUiEvent.OnEditGigInfoClicked ->
                viewModelScope.launch { _event.emit(GigDetailEvent.NavigateToEditGig) }
            GigDetailUiEvent.OnDeleteGigClicked -> viewModelScope.launch {
                deleteGig(gigId)
                _event.emit(GigDetailEvent.NavigateBack)
            }
            GigDetailUiEvent.OnAddSongsClicked ->
                _uiState.update { it.copy(showAddSongsSheet = true) }
            GigDetailUiEvent.OnAddSongsDismissed ->
                _uiState.update { it.copy(showAddSongsSheet = false) }
            is GigDetailUiEvent.OnSongAddedToSetlist -> addSongToSetlist(event.songId)
            is GigDetailUiEvent.OnSongRemovedFromSetlist -> removeSongFromSetlist(event.songId)
            is GigDetailUiEvent.OnSetlistReordered -> persistSetlist(event.newOrder)
        }
    }

    // ── Setlist mutations — write to DB only; state is driven by the observable ──

    private fun addSongToSetlist(songId: SongId) {
        val currentIds = _uiState.value.setlistSongs.map { it.id }
        if (songId in currentIds) return
        persistSetlist(currentIds + songId)
    }

    private fun removeSongFromSetlist(songId: SongId) {
        val currentIds = _uiState.value.setlistSongs.map { it.id }
        persistSetlist(currentIds.filter { it != songId })
    }

    /** Persists [orderedSongIds] to the DB. The gig observable picks up the change and
     *  calls [refreshDerivedSongLists], which is the only place that updates setlist state. */
    private fun persistSetlist(orderedSongIds: List<SongId>) {
        val gig = currentGig ?: return
        viewModelScope.launch {
            updateGig(gig.copy(orderedSongIds = orderedSongIds))
        }
    }
}
