package com.amolieres.setlistync.feature.band.gig.edit.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.CreateGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.DeleteGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.GetGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveGigsForBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateGigUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GigEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val getGig: GetGigUseCase,
    private val createGig: CreateGigUseCase,
    private val updateGig: UpdateGigUseCase,
    private val observeGigsForBand: ObserveGigsForBandUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))
    val gigId: String? = savedStateHandle.get<String>("gigId")
    val isEditMode: Boolean = gigId != null

    private val _event = MutableSharedFlow<GigEditEvent>()
    val event: SharedFlow<GigEditEvent> = _event.asSharedFlow()

    private val _uiState = MutableStateFlow(GigEditUiState(isLoading = gigId != null))
    val uiState: StateFlow<GigEditUiState> = _uiState

    init {
        if (gigId != null) {
            viewModelScope.launch {
                val gig = getGig(gigId)
                _uiState.update { state ->
                    if (gig != null) {
                        state.copy(
                            isLoading = false,
                            venueInput = gig.venue ?: "",
                            dateMillis = gig.date?.let { d ->
                                @OptIn(ExperimentalTime::class)
                                d.epochSeconds * 1_000L + d.nanosecondsOfSecond / 1_000_000
                            },
                            expectedDurationInput = gig.expectedDurationMinutes?.toString() ?: ""
                        )
                    } else {
                        state.copy(isLoading = false)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                observeGigsForBand(bandId).collect { gigs ->
                    _uiState.update { it.copy(gigsForImport = gigs) }
                }
            }
        }
    }

    fun onScreenEvent(event: GigEditUiEvent) {
        when (event) {
            is GigEditUiEvent.OnVenueChanged ->
                _uiState.update { it.copy(venueInput = event.venue) }
            is GigEditUiEvent.OnExpectedDurationChanged ->
                _uiState.update { it.copy(expectedDurationInput = event.minutes) }
            GigEditUiEvent.OnDatePickerOpen ->
                _uiState.update { it.copy(showDatePicker = true) }
            GigEditUiEvent.OnDatePickerDismissed ->
                _uiState.update { it.copy(showDatePicker = false) }
            is GigEditUiEvent.OnDateSelected ->
                _uiState.update { it.copy(dateMillis = event.epochMillis, showDatePicker = false) }
            GigEditUiEvent.OnSaveClicked -> doSave()
            GigEditUiEvent.OnImportClicked ->
                _uiState.update { it.copy(showImportSheet = true) }
            GigEditUiEvent.OnImportDismissed ->
                _uiState.update { it.copy(showImportSheet = false) }
            is GigEditUiEvent.OnImportGigSelected -> doImportGig(event.gigId)
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun doImportGig(gigId: String) {
        val gig = _uiState.value.gigsForImport.find { it.id == gigId } ?: return
        _uiState.update { state ->
            state.copy(
                showImportSheet = false,
                venueInput = gig.venue ?: state.venueInput,
                dateMillis = gig.date?.let { d ->
                    d.epochSeconds * 1_000L + d.nanosecondsOfSecond / 1_000_000
                } ?: state.dateMillis,
                expectedDurationInput = gig.expectedDurationMinutes?.toString()
                    ?: state.expectedDurationInput,
                importedSets = gig.sets
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun doSave() {
        val state = _uiState.value
        if (state.isSaving) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val date = state.dateMillis?.let { Instant.fromEpochMilliseconds(it) }
            val expectedDuration = state.expectedDurationInput.trim().toIntOrNull()
            val venue = state.venueInput.trim().ifEmpty { null }

            if (gigId == null) {
                // Create mode → create then navigate to GigDetail
                createGig(
                    bandId = bandId,
                    venue = venue,
                    date = date,
                    expectedDurationMinutes = expectedDuration,
                    sets = state.importedSets
                ).onSuccess { gig ->
                    _event.emit(GigEditEvent.NavigateToGigDetail(gig.id))
                }.onFailure {
                    _uiState.update { it.copy(isSaving = false) }
                }
            } else {
                // Edit mode → update then go back to GigDetail
                val existing = getGig(gigId)
                if (existing != null) {
                    updateGig(
                        existing.copy(
                            venue = venue,
                            date = date,
                            expectedDurationMinutes = expectedDuration
                        )
                    )
                }
                _event.emit(GigEditEvent.NavigateBack)
            }
        }
    }
}
