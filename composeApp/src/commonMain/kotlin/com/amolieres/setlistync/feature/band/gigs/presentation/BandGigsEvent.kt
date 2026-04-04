package com.amolieres.setlistync.feature.band.gigs.presentation

sealed interface BandGigsEvent {
    data object NavigateBack : BandGigsEvent
    data object NavigateToNewGig : BandGigsEvent
    data class NavigateToGigDetail(val gigId: String) : BandGigsEvent
}
