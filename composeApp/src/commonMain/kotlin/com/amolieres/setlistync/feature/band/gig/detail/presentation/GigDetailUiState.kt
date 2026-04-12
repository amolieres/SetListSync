package com.amolieres.setlistync.feature.band.gig.detail.presentation

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.song.model.Song

data class GigDetailUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val gig: Gig? = null,
    val setlistDurationSeconds: Int = 0,
    // Setlist
    val setlistSongs: List<Song> = emptyList(),
    val catalogSongs: List<Song> = emptyList(),
    // UI dialogs
    val showAddSongsSheet: Boolean = false,
    // Preferences
    val noteNotation: NoteNotation = NoteNotation.FR
)
