package com.amolieres.setlistync.feature.band.detail.presentation

sealed interface BandDetailUiEvent {
    object OnMembersSectionClicked : BandDetailUiEvent
    object OnDeleteBandClicked : BandDetailUiEvent
    object OnDeleteBandConfirmed : BandDetailUiEvent
    object OnDeleteBandDismiss : BandDetailUiEvent
}
