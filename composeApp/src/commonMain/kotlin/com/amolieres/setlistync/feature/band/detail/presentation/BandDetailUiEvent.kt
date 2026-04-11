package com.amolieres.setlistync.feature.band.detail.presentation

sealed interface BandDetailUiEvent {
    object OnEditInfoClicked : BandDetailUiEvent
    object OnMembersSectionClicked : BandDetailUiEvent
    object OnSongsSectionClicked : BandDetailUiEvent
    object OnAddGigClicked : BandDetailUiEvent
    data class OnGigClicked(val gigId: String) : BandDetailUiEvent
    data class OnDeleteGigClicked(val gigId: String) : BandDetailUiEvent
    object OnDeleteBandClicked : BandDetailUiEvent
    object OnDeleteBandConfirmed : BandDetailUiEvent
    object OnDeleteBandDismiss : BandDetailUiEvent
}
