package com.amolieres.setlistync.feature.band.songs.presentation

import com.amolieres.setlistync.core.domain.song.model.Song

data class BandSongsUiState(
    val isLoading: Boolean = true,
    val bandName: String = "",
    val songs: List<Song> = emptyList(),
    val showSongDialog: Boolean = false,
    val songTitle: String = "",
    val songMinutes: String = "",
    val songSeconds: String = "",
    val songKey: String = "",
    val songTempo: String = "",
    val isSavingSong: Boolean = false
)
