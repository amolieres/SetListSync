package com.amolieres.setlistync.feature.band.gig.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.Gig
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
    observeNotation: ObserveNotationUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))
    val gigId: String = checkNotNull(savedStateHandle.get<String>("gigId"))
    private val initialIsEditing: Boolean = savedStateHandle.get<Boolean>("isEditing") ?: false

    private val _event = MutableSharedFlow<GigDetailEvent>()
    val event: SharedFlow<GigDetailEvent> = _event.asSharedFlow()

    private val _uiState = MutableStateFlow(GigDetailUiState(isEditing = initialIsEditing))
    val uiState: StateFlow<GigDetailUiState> = _uiState

    // Latest snapshot of band songs and current gig — used for setlist persistence
    private var allSongs: List<Song> = emptyList()
    private var currentGig: Gig? = null

    // Pending ordered IDs from the gig, applied once songs are loaded
    private var pendingOrderedIds: List<SongId> = emptyList()

    init {
        // Observe the specific gig (live updates after GigEdit saves)
        viewModelScope.launch {
            observeGigsForBand(bandId).collect { gigs ->
                val gig = gigs.find { it.id == gigId }
                currentGig = gig
                if (gig != null) {
                    // If this is the first gig emission, schedule setlist population
                    // once songs are loaded; or apply immediately if already loaded.
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

    private fun refreshDerivedSongLists(orderedIds: List<SongId>) {
        val songMap = allSongs.associateBy { it.id }
        val setlist = orderedIds.mapNotNull { songMap[it] }
        val setlistIds = setlist.map { it.id }.toSet()
        val catalog = allSongs.filter { it.id !in setlistIds }
        _uiState.update { it.copy(setlistSongs = setlist, catalogSongs = catalog) }
    }

    fun onScreenEvent(event: GigDetailUiEvent) {
        when (event) {
            GigDetailUiEvent.OnToggleEditing ->
                _uiState.update { it.copy(isEditing = !it.isEditing) }
            GigDetailUiEvent.OnEditGigInfoClicked ->
                viewModelScope.launch { _event.emit(GigDetailEvent.NavigateToEditGig) }
            GigDetailUiEvent.OnAddSongsClicked ->
                _uiState.update { it.copy(showAddSongsSheet = true) }
            GigDetailUiEvent.OnAddSongsDismissed ->
                _uiState.update { it.copy(showAddSongsSheet = false) }
            is GigDetailUiEvent.OnSongAddedToSetlist -> addSongToSetlist(event.songId)
            is GigDetailUiEvent.OnSongRemovedFromSetlist -> removeSongFromSetlist(event.songId)
            is GigDetailUiEvent.OnSetlistReordered -> reorderSetlist(event.newOrder)
        }
    }

    private fun addSongToSetlist(songId: SongId) {
        val song = allSongs.find { it.id == songId } ?: return
        _uiState.update { state ->
            state.copy(
                setlistSongs = state.setlistSongs + song,
                catalogSongs = state.catalogSongs.filter { it.id != songId }
            )
        }
        persistSetlist()
    }

    private fun removeSongFromSetlist(songId: SongId) {
        val song = allSongs.find { it.id == songId } ?: return
        _uiState.update { state ->
            state.copy(
                setlistSongs = state.setlistSongs.filter { it.id != songId },
                catalogSongs = (state.catalogSongs + song).sortedBy { it.title }
            )
        }
        persistSetlist()
    }

    private fun reorderSetlist(newOrder: List<SongId>) {
        val songMap = allSongs.associateBy { it.id }
        _uiState.update { it.copy(setlistSongs = newOrder.mapNotNull { songMap[it] }) }
        persistSetlist()
    }

    private fun persistSetlist() {
        val gig = currentGig ?: return
        val songs = _uiState.value.setlistSongs
        viewModelScope.launch {
            updateGig(gig.copy(orderedSongIds = songs.map { it.id }))
        }
    }
}
