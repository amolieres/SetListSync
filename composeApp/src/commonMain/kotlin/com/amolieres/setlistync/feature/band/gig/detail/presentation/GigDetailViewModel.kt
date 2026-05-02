package com.amolieres.setlistync.feature.band.gig.detail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.model.GigSet
import com.amolieres.setlistync.core.domain.band.usecase.AddSetToGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ComputeSetlistDurationUseCase
import com.amolieres.setlistync.core.domain.band.usecase.DeleteGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveGigsForBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.RemoveSetFromGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.RenameGigSetUseCase
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
    private val deleteGig: DeleteGigUseCase,
    private val addSetToGig: AddSetToGigUseCase,
    private val removeSetFromGig: RemoveSetFromGigUseCase,
    private val renameGigSet: RenameGigSetUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))
    val gigId: String = checkNotNull(savedStateHandle.get<String>("gigId"))
    private val initialIsEditing: Boolean = savedStateHandle.get<Boolean>("isEditing") ?: false

    private val _event = MutableSharedFlow<GigDetailEvent>()
    val event: SharedFlow<GigDetailEvent> = _event.asSharedFlow()

    private val _uiState = MutableStateFlow(GigDetailUiState(isEditing = initialIsEditing))
    val uiState: StateFlow<GigDetailUiState> = _uiState

    // Latest snapshot of all band songs and current gig
    private var allSongs: List<Song> = emptyList()
    private var currentGig: Gig? = null

    // Gig sets received before songs have loaded
    private var gigSetsBeforeSongsLoaded: List<GigSet>? = null

    init {
        // Observe the specific gig
        viewModelScope.launch {
            observeGigsForBand(bandId).collect { gigs ->
                val gig = gigs.find { it.id == gigId }
                currentGig = gig
                if (gig != null) {
                    if (allSongs.isEmpty()) {
                        gigSetsBeforeSongsLoaded = gig.sets
                    } else {
                        refreshDerivedSetsState(gig.sets)
                    }
                }
                _uiState.update { it.copy(isLoading = false, gig = gig) }
            }
        }

        // Observe band songs
        viewModelScope.launch {
            observeSongs(bandId).collect { songs ->
                allSongs = songs
                val setsToRefresh = gigSetsBeforeSongsLoaded
                    ?: currentGig?.sets
                    ?: emptyList()
                gigSetsBeforeSongsLoaded = null
                refreshDerivedSetsState(setsToRefresh)
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
     * Rebuilds the sets UI state from the domain [sets] and [allSongs].
     * Also refreshes catalog if a song-add sheet is open.
     */
    private fun refreshDerivedSetsState(sets: List<GigSet>) {
        val songMap = allSongs.associateBy { it.id }
        val gigSetUiStates = sets.map { set ->
            val songs = set.orderedSongIds.mapNotNull { songMap[it] }
            GigDetailUiState.GigSetUiState(
                setId = set.id,
                title = set.title,
                songs = songs,
                durationSeconds = computeSetlistDuration(songs)
            )
        }
        val totalDuration = gigSetUiStates.sumOf { it.durationSeconds }
        // Refresh catalog if the add-song sheet is open
        val addingSongsToSetId = _uiState.value.addingSongsToSetId
        val catalog = if (addingSongsToSetId != null) {
            val idsInSet = sets.find { it.id == addingSongsToSetId }
                ?.orderedSongIds?.toSet() ?: emptySet()
            allSongs.filter { it.id !in idsInSet }
        } else {
            _uiState.value.catalogSongs
        }
        _uiState.update {
            it.copy(
                sets = gigSetUiStates,
                totalDurationSeconds = totalDuration,
                catalogSongs = catalog
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
            is GigDetailUiEvent.OnAddSongsClicked -> openAddSongsSheet(event.setId)
            GigDetailUiEvent.OnAddSongsDismissed ->
                _uiState.update { it.copy(addingSongsToSetId = null) }
            is GigDetailUiEvent.OnSongAddedToSetlist -> addSongToSet(event.setId, event.songId)
            is GigDetailUiEvent.OnSongRemovedFromSetlist -> removeSongFromSet(event.setId, event.songId)
            is GigDetailUiEvent.OnSetlistReordered -> reorderSet(event.setId, event.newOrder)
            GigDetailUiEvent.OnAddSetClicked -> viewModelScope.launch { addSetToGig(gigId) }
            is GigDetailUiEvent.OnRemoveSetClicked -> viewModelScope.launch { removeSetFromGig(gigId, event.setId) }
            is GigDetailUiEvent.OnEditSetTitleClicked -> openEditSetTitleDialog(event.setId)
            is GigDetailUiEvent.OnSetTitleChanged ->
                _uiState.update { it.copy(editingSetTitleInput = event.title) }
            GigDetailUiEvent.OnSetTitleConfirmed -> confirmSetTitle()
            GigDetailUiEvent.OnSetTitleDismissed ->
                _uiState.update { it.copy(editingSetTitleSetId = null) }
        }
    }

    // ── Catalog / sheet ───────────────────────────────────────────────────────

    private fun openAddSongsSheet(setId: String) {
        val idsInSet = currentGig?.sets?.find { it.id == setId }
            ?.orderedSongIds?.toSet() ?: emptySet()
        val catalog = allSongs.filter { it.id !in idsInSet }
        _uiState.update { it.copy(addingSongsToSetId = setId, catalogSongs = catalog) }
    }

    // ── Setlist mutations — write to DB; state driven by observable ────────

    private fun addSongToSet(setId: String, songId: SongId) {
        val gig = currentGig ?: return
        val updatedSets = gig.sets.map { set ->
            if (set.id == setId && songId !in set.orderedSongIds) {
                set.copy(orderedSongIds = set.orderedSongIds + songId)
            } else set
        }
        persistSets(gig, updatedSets)
        // Optimistically refresh catalog so just-added song disappears from sheet
        val updatedSet = updatedSets.find { it.id == setId }
        val idsInSet = updatedSet?.orderedSongIds?.toSet() ?: emptySet()
        _uiState.update { it.copy(catalogSongs = allSongs.filter { s -> s.id !in idsInSet }) }
    }

    private fun removeSongFromSet(setId: String, songId: SongId) {
        val gig = currentGig ?: return
        val updatedSets = gig.sets.map { set ->
            if (set.id == setId) set.copy(orderedSongIds = set.orderedSongIds - songId) else set
        }
        persistSets(gig, updatedSets)
    }

    private fun reorderSet(setId: String, newOrder: List<SongId>) {
        val gig = currentGig ?: return
        val updatedSets = gig.sets.map { set ->
            if (set.id == setId) set.copy(orderedSongIds = newOrder) else set
        }
        persistSets(gig, updatedSets)
    }

    private fun persistSets(gig: Gig, sets: List<GigSet>) {
        viewModelScope.launch { updateGig(gig.copy(sets = sets)) }
    }

    // ── Set title dialog ──────────────────────────────────────────────────────

    private fun openEditSetTitleDialog(setId: String) {
        val currentTitle = currentGig?.sets?.find { it.id == setId }?.title ?: ""
        _uiState.update {
            it.copy(editingSetTitleSetId = setId, editingSetTitleInput = currentTitle)
        }
    }

    private fun confirmSetTitle() {
        val state = _uiState.value
        val setId = state.editingSetTitleSetId ?: return
        _uiState.update { it.copy(editingSetTitleSetId = null) }
        viewModelScope.launch {
            renameGigSet(gigId, setId, state.editingSetTitleInput.trim().ifEmpty { null })
        }
    }
}
