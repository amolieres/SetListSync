package com.amolieres.setlistync.feature.band.songs.presentation

import com.amolieres.setlistync.core.domain.song.model.SongId

sealed interface BandSongsUiEvent {
    data object OnAddSongClicked : BandSongsUiEvent
    data class OnEditSongClicked(val songId: SongId) : BandSongsUiEvent
    data class OnDeleteSongClicked(val songId: SongId) : BandSongsUiEvent
}
