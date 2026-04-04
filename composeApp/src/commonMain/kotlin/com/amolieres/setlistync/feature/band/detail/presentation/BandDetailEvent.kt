package com.amolieres.setlistync.feature.band.detail.presentation

sealed interface BandDetailEvent {
    object NavigateBack : BandDetailEvent
    object NavigateToMembers : BandDetailEvent
    object NavigateToSongs : BandDetailEvent
    object NavigateToGigs : BandDetailEvent
    object NavigateToEdit : BandDetailEvent
}
