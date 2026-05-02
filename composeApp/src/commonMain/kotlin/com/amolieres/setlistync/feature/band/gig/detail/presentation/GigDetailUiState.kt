package com.amolieres.setlistync.feature.band.gig.detail.presentation

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.song.model.Song

data class GigDetailUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val gig: Gig? = null,
    val totalDurationSeconds: Int = 0,
    // Multi-set state
    val sets: List<GigSetUiState> = emptyList(),
    val catalogSongs: List<Song> = emptyList(),
    // Which set's add-song sheet is open (null = closed)
    val addingSongsToSetId: String? = null,
    // Set title editing dialog
    val editingSetTitleSetId: String? = null,
    val editingSetTitleInput: String = "",
    // Preferences
    val noteNotation: NoteNotation = NoteNotation.FR
) {
    data class GigSetUiState(
        val setId: String,
        val title: String?,
        val songs: List<Song>,
        val durationSeconds: Int
    )
}
