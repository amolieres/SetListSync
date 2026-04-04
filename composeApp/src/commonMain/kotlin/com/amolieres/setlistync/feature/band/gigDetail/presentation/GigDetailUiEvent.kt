package com.amolieres.setlistync.feature.band.gigDetail.presentation

import com.amolieres.setlistync.core.domain.song.model.SongId

sealed interface GigDetailUiEvent {
    // Form fields
    data class OnVenueChanged(val venue: String) : GigDetailUiEvent
    data class OnExpectedDurationChanged(val minutes: String) : GigDetailUiEvent
    // Date picker
    data object OnDatePickerOpen : GigDetailUiEvent
    data object OnDatePickerDismissed : GigDetailUiEvent
    data class OnDateSelected(val epochMillis: Long?) : GigDetailUiEvent
    // Setlist
    data object OnAddSongsClicked : GigDetailUiEvent
    data object OnAddSongsDismissed : GigDetailUiEvent
    data class OnSongAddedToSetlist(val songId: SongId) : GigDetailUiEvent
    data class OnSongRemovedFromSetlist(val songId: SongId) : GigDetailUiEvent
    data class OnSetlistReordered(val newOrder: List<SongId>) : GigDetailUiEvent
    // Save
    data object OnSaveClicked : GigDetailUiEvent
}
