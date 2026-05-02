package com.amolieres.setlistync.feature.band.gig.detail.presentation

import com.amolieres.setlistync.core.domain.song.model.SongId

sealed interface GigDetailUiEvent {
    data object OnToggleEditing : GigDetailUiEvent
    data object OnEditGigInfoClicked : GigDetailUiEvent
    data object OnDeleteGigClicked : GigDetailUiEvent
    // Setlist — per-set operations
    data class OnAddSongsClicked(val setId: String) : GigDetailUiEvent
    data object OnAddSongsDismissed : GigDetailUiEvent
    data class OnSongAddedToSetlist(val setId: String, val songId: SongId) : GigDetailUiEvent
    data class OnSongRemovedFromSetlist(val setId: String, val songId: SongId) : GigDetailUiEvent
    data class OnSetlistReordered(val setId: String, val newOrder: List<SongId>) : GigDetailUiEvent
    // Set management
    data object OnAddSetClicked : GigDetailUiEvent
    data class OnRemoveSetClicked(val setId: String) : GigDetailUiEvent
    data class OnEditSetTitleClicked(val setId: String) : GigDetailUiEvent
    data class OnSetTitleChanged(val title: String) : GigDetailUiEvent
    data object OnSetTitleConfirmed : GigDetailUiEvent
    data object OnSetTitleDismissed : GigDetailUiEvent
}
