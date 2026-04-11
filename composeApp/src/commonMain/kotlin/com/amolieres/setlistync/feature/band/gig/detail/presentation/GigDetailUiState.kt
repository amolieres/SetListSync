package com.amolieres.setlistync.feature.band.gig.detail.presentation

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.domain.song.model.Song

data class GigDetailUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,
    // Form fields
    val venueInput: String = "",
    val dateMillis: Long? = null,        // epoch ms; null = no date
    val expectedDurationInput: String = "",
    // Setlist
    val setlistSongs: List<Song> = emptyList(),   // songs already in setlist (ordered)
    val catalogSongs: List<Song> = emptyList(),   // all band songs NOT in setlist
    // UI dialogs
    val showDatePicker: Boolean = false,
    val showAddSongsSheet: Boolean = false,
    // Preferences
    val noteNotation: NoteNotation = NoteNotation.FR  // matches DataStore default
)
