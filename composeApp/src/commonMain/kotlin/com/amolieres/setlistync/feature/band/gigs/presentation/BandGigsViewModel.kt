package com.amolieres.setlistync.feature.band.gigs.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.DeleteGigUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveGigsForBandUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BandGigsViewModel(
    savedStateHandle: SavedStateHandle,
    observeBand: ObserveBandUseCase,
    observeGigs: ObserveGigsForBandUseCase,
    private val deleteGig: DeleteGigUseCase
) : ViewModel() {

    val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _event = MutableSharedFlow<BandGigsEvent>()
    val event: SharedFlow<BandGigsEvent> = _event.asSharedFlow()

    val uiState: StateFlow<BandGigsUiState> = combine(
        observeBand(bandId),
        observeGigs(bandId)
    ) { band, gigs ->
        BandGigsUiState(
            isLoading = false,
            bandName = band?.name ?: "",
            gigs = gigs
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandGigsUiState())

    fun onScreenEvent(event: BandGigsUiEvent) {
        when (event) {
            BandGigsUiEvent.OnAddGigClicked ->
                viewModelScope.launch { _event.emit(BandGigsEvent.NavigateToNewGig) }
            is BandGigsUiEvent.OnGigClicked ->
                viewModelScope.launch { _event.emit(BandGigsEvent.NavigateToGigDetail(event.gigId)) }
            is BandGigsUiEvent.OnDeleteGigClicked -> doDeleteGig(event.gigId)
        }
    }

    private fun doDeleteGig(gigId: String) {
        viewModelScope.launch { deleteGig(gigId) }
    }
}
