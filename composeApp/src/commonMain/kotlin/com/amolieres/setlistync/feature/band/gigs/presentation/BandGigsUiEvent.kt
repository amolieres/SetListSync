package com.amolieres.setlistync.feature.band.gigs.presentation

sealed interface BandGigsUiEvent {
    data object OnAddGigClicked : BandGigsUiEvent
    data class OnGigClicked(val gigId: String) : BandGigsUiEvent
    data class OnDeleteGigClicked(val gigId: String) : BandGigsUiEvent
}
