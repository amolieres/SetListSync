package com.amolieres.setlistync.feature.band.songDetail.presentation

import com.amolieres.setlistync.core.domain.song.model.SongSearchResult

sealed interface BandSongDetailUiEvent {
    data class OnTitleChanged(val title: String) : BandSongDetailUiEvent
    data class OnMinutesChanged(val minutes: String) : BandSongDetailUiEvent
    data class OnSecondsChanged(val seconds: String) : BandSongDetailUiEvent
    data class OnKeyChanged(val key: String) : BandSongDetailUiEvent
    data class OnTempoChanged(val tempo: String) : BandSongDetailUiEvent
    data class OnOriginalArtistChanged(val artist: String) : BandSongDetailUiEvent
    data object OnSaveClicked : BandSongDetailUiEvent
    data object OnBackClicked : BandSongDetailUiEvent
    data class OnSearchQueryChanged(val query: String) : BandSongDetailUiEvent
    data object OnSearchSubmitted : BandSongDetailUiEvent
    data class OnSearchResultSelected(val result: SongSearchResult) : BandSongDetailUiEvent
    data object OnSearchResultsDismissed : BandSongDetailUiEvent
}
