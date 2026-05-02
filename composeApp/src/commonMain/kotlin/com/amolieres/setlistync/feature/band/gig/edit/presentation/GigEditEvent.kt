package com.amolieres.setlistync.feature.band.gig.edit.presentation

sealed interface GigEditEvent {
    data object NavigateBack : GigEditEvent
    data class NavigateToGigDetail(val gigId: String) : GigEditEvent
}
