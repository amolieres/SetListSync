package com.amolieres.setlistync.feature.band.songDetail.presentation

sealed interface BandSongDetailUiEvent {
    data class OnTitleChanged(val title: String) : BandSongDetailUiEvent
    data class OnMinutesChanged(val minutes: String) : BandSongDetailUiEvent
    data class OnSecondsChanged(val seconds: String) : BandSongDetailUiEvent
    data class OnKeyChanged(val key: String) : BandSongDetailUiEvent
    data class OnTempoChanged(val tempo: String) : BandSongDetailUiEvent
    data class OnOriginalArtistChanged(val artist: String) : BandSongDetailUiEvent
    data object OnSaveClicked : BandSongDetailUiEvent
    data object OnBackClicked : BandSongDetailUiEvent
}
