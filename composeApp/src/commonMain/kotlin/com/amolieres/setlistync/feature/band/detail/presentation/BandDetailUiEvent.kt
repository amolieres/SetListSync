package com.amolieres.setlistync.feature.band.detail.presentation

sealed interface BandDetailUiEvent {
    object OnEditInfoClicked : BandDetailUiEvent
    object OnMembersSectionClicked : BandDetailUiEvent
    object OnSongsSectionClicked : BandDetailUiEvent
    object OnGigsSectionClicked : BandDetailUiEvent
    object OnDeleteBandClicked : BandDetailUiEvent
    object OnDeleteBandConfirmed : BandDetailUiEvent
    object OnDeleteBandDismiss : BandDetailUiEvent
}
