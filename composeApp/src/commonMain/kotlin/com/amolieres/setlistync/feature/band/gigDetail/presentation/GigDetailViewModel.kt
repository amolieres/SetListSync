package com.amolieres.setlistync.feature.band.gigDetail.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.usecase.CreateGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.GetGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateGigUseCase
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
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GigDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGig: GetGigUseCase,
    private val createGig: CreateGigUseCase,
    private val updateGig: UpdateGigUseCase,
    observeSongs: ObserveSongsUseCase
) : ViewModel() {

    private val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))
    private val gigId: String? = savedStateHandle.get<String>("gigId")
    val isEditMode: Boolean = gigId != null

    private val _event = MutableSharedFlow<GigDetailEvent>()
    val event: SharedFlow<GigDetailEvent> = _event.asSharedFlow()

    private val _uiState = MutableStateFlow(GigDetailUiState(isLoading = gigId != null))
    val uiState: StateFlow<GigDetailUiState> = _uiState

    // Keep a snapshot of all band songs for catalog / setlist resolution
    private var allSongs: List<Song> = emptyList()

    init {
        // Observe all band songs to keep allSongs up-to-date and recompute derived lists
        viewModelScope.launch {
            observeSongs(bandId).collect { songs ->
                allSongs = songs
                refreshDerivedSongLists()
            }
        }
        // Load existing gig in edit mode
        if (gigId != null) {
            viewModelScope.launch {
                val gig = getGig(gigId)
                if (gig != null) populateFrom(gig)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun populateFrom(gig: Gig) {
        _uiState.update { state ->
            state.copy(
                venueInput = gig.venue ?: "",
                dateMillis = gig.date?.let { it.epochSeconds * 1000L + it.nanosecondsOfSecond / 1_000_000 },
                expectedDurationInput = gig.expectedDurationMinutes?.toString() ?: ""
            )
        }
        // orderedSongIds are used in refreshDerivedSongLists once allSongs loads
        _currentOrderedIds = gigId?.let { _ ->
            // We'll restore from gig; we need the gig reference
        }.let { emptyList<SongId>() }
        // Store ordered ids for later use when songs load
        _pendingOrderedIds = gig.orderedSongIds
    }

    private var _pendingOrderedIds: List<SongId> = emptyList()
    // current ordered IDs as maintained by the UI
    private var _currentOrderedIds: List<SongId>
        get() = _uiState.value.setlistSongs.map { it.id }
        set(_) {}  // set via _pendingOrderedIds / direct state updates

    private fun refreshDerivedSongLists() {
        val currentOrdered = if (_pendingOrderedIds.isNotEmpty()) {
            // Apply pending ordered ids from loaded gig
            val result = _pendingOrderedIds
            _pendingOrderedIds = emptyList()
            result
        } else {
            _uiState.value.setlistSongs.map { it.id }
        }

        val songMap = allSongs.associateBy { it.id }
        val setlist = currentOrdered.mapNotNull { songMap[it] }
        val setlistIds = setlist.map { it.id }.toSet()
        val catalog = allSongs.filter { it.id !in setlistIds }

        _uiState.update { it.copy(setlistSongs = setlist, catalogSongs = catalog) }
    }

    fun onScreenEvent(event: GigDetailUiEvent) {
        when (event) {
            is GigDetailUiEvent.OnVenueChanged ->
                _uiState.update { it.copy(venueInput = event.venue) }
            is GigDetailUiEvent.OnExpectedDurationChanged ->
                _uiState.update { it.copy(expectedDurationInput = event.minutes) }
            GigDetailUiEvent.OnDatePickerOpen ->
                _uiState.update { it.copy(showDatePicker = true) }
            GigDetailUiEvent.OnDatePickerDismissed ->
                _uiState.update { it.copy(showDatePicker = false) }
            is GigDetailUiEvent.OnDateSelected -> {
                _uiState.update { it.copy(dateMillis = event.epochMillis, showDatePicker = false) }
            }
            GigDetailUiEvent.OnAddSongsClicked ->
                _uiState.update { it.copy(showAddSongsSheet = true) }
            GigDetailUiEvent.OnAddSongsDismissed ->
                _uiState.update { it.copy(showAddSongsSheet = false) }
            is GigDetailUiEvent.OnSongAddedToSetlist -> addSongToSetlist(event.songId)
            is GigDetailUiEvent.OnSongRemovedFromSetlist -> removeSongFromSetlist(event.songId)
            is GigDetailUiEvent.OnSetlistReordered -> reorderSetlist(event.newOrder)
            GigDetailUiEvent.OnSaveClicked -> doSave()
        }
    }

    private fun addSongToSetlist(songId: SongId) {
        val song = allSongs.find { it.id == songId } ?: return
        _uiState.update { state ->
            val newSetlist = state.setlistSongs + song
            state.copy(
                setlistSongs = newSetlist,
                catalogSongs = state.catalogSongs.filter { it.id != songId }
            )
        }
    }

    private fun removeSongFromSetlist(songId: SongId) {
        val song = allSongs.find { it.id == songId } ?: return
        _uiState.update { state ->
            val newSetlist = state.setlistSongs.filter { it.id != songId }
            state.copy(
                setlistSongs = newSetlist,
                catalogSongs = (state.catalogSongs + song).sortedBy { it.title }
            )
        }
    }

    private fun reorderSetlist(newOrder: List<SongId>) {
        val songMap = allSongs.associateBy { it.id }
        val reordered = newOrder.mapNotNull { songMap[it] }
        _uiState.update { it.copy(setlistSongs = reordered) }
    }

    @OptIn(ExperimentalTime::class)
    private fun doSave() {
        val state = _uiState.value
        if (state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val date = state.dateMillis?.let { Instant.fromEpochMilliseconds(it) }
            val expectedDuration = state.expectedDurationInput.trim().toIntOrNull()
            val orderedIds = state.setlistSongs.map { it.id }

            if (gigId == null) {
                // Create mode
                createGig(
                    bandId = bandId,
                    venue = state.venueInput.trim().ifEmpty { null },
                    date = date,
                    expectedDurationMinutes = expectedDuration
                ).onSuccess { gig ->
                    // Apply setlist if any songs were added
                    if (orderedIds.isNotEmpty()) {
                        updateGig(gig.copy(orderedSongIds = orderedIds))
                    }
                }
            } else {
                // Edit mode
                val existing = getGig(gigId)
                if (existing != null) {
                    updateGig(
                        existing.copy(
                            venue = state.venueInput.trim().ifEmpty { null },
                            date = date,
                            expectedDurationMinutes = expectedDuration,
                            orderedSongIds = orderedIds
                        )
                    )
                }
            }

            _event.emit(GigDetailEvent.NavigateBack)
        }
    }
}
