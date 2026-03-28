package com.amolieres.setlistync.feature.band.creation.presentation

sealed interface BandCreationEvent {
    object NavigateBack : BandCreationEvent
    data class NavigateToBandDetail(val bandId: String) : BandCreationEvent
}
