package com.amolieres.setlistync.feature.band.edit.presentation

sealed interface BandEditUiEvent {
    data class OnNameChanged(val value: String) : BandEditUiEvent
    data class OnEmailChanged(val value: String) : BandEditUiEvent
    data class OnInstagramChanged(val value: String) : BandEditUiEvent
    data class OnFacebookChanged(val value: String) : BandEditUiEvent
    data class OnTiktokChanged(val value: String) : BandEditUiEvent
    data class OnGenreInputChanged(val value: String) : BandEditUiEvent
    object OnAddGenreClicked : BandEditUiEvent
    data class OnRemoveGenre(val genre: String) : BandEditUiEvent
    object OnSaveClicked : BandEditUiEvent
    object OnBackClicked : BandEditUiEvent
}
