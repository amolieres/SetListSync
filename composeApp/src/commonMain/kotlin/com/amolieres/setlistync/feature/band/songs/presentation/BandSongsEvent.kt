package com.amolieres.setlistync.feature.band.songs.presentation

import com.amolieres.setlistync.core.domain.song.model.SongId

sealed interface BandSongsEvent {
    data object NavigateBack : BandSongsEvent
    data object NavigateToNewSong : BandSongsEvent
    data class NavigateToEditSong(val songId: SongId) : BandSongsEvent
}
