package com.amolieres.setlistync.feature.band.songs.presentation

import com.amolieres.setlistync.core.domain.song.model.SongId

sealed interface BandSongsUiEvent {
    object OnAddSongClicked : BandSongsUiEvent
    data class OnSongTitleChanged(val title: String) : BandSongsUiEvent
    data class OnSongMinutesChanged(val minutes: String) : BandSongsUiEvent
    data class OnSongSecondsChanged(val seconds: String) : BandSongsUiEvent
    data class OnSongKeyChanged(val key: String) : BandSongsUiEvent
    data class OnSongTempoChanged(val tempo: String) : BandSongsUiEvent
    object OnSongDialogConfirmed : BandSongsUiEvent
    object OnSongDialogDismiss : BandSongsUiEvent
    data class OnDeleteSongClicked(val songId: SongId) : BandSongsUiEvent
}
