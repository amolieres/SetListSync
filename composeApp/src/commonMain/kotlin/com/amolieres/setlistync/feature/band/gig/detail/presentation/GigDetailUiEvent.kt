package com.amolieres.setlistync.feature.band.gig.detail.presentation

import com.amolieres.setlistync.core.domain.song.model.SongId

sealed interface GigDetailUiEvent {
    data object OnEditGigClicked : GigDetailUiEvent
    // Setlist
    data object OnAddSongsClicked : GigDetailUiEvent
    data object OnAddSongsDismissed : GigDetailUiEvent
    data class OnSongAddedToSetlist(val songId: SongId) : GigDetailUiEvent
    data class OnSongRemovedFromSetlist(val songId: SongId) : GigDetailUiEvent
    data class OnSetlistReordered(val newOrder: List<SongId>) : GigDetailUiEvent
}
