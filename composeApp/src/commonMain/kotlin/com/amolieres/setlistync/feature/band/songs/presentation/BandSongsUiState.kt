package com.amolieres.setlistync.feature.band.songs.presentation

import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.core.domain.song.model.Song

data class BandSongsUiState(
    val isLoading: Boolean = true,
    val bandName: String = "",
    val songs: List<Song> = emptyList(),
    val noteNotation: NoteNotation = NoteNotation.EN
)
