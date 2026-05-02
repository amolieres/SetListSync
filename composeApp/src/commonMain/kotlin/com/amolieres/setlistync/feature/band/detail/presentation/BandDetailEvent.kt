package com.amolieres.setlistync.feature.band.detail.presentation

sealed interface BandDetailEvent {
    object NavigateBack : BandDetailEvent
    object NavigateToMembers : BandDetailEvent
    object NavigateToSongs : BandDetailEvent
    object NavigateToNewGig : BandDetailEvent
    data class NavigateToGigDetail(val gigId: String) : BandDetailEvent
    object NavigateToEdit : BandDetailEvent
}
