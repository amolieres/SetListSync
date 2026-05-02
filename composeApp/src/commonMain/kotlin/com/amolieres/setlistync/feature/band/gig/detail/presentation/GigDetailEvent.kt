package com.amolieres.setlistync.feature.band.gig.detail.presentation

sealed interface GigDetailEvent {
    data object NavigateBack : GigDetailEvent
    data object NavigateToEditGig : GigDetailEvent
}
