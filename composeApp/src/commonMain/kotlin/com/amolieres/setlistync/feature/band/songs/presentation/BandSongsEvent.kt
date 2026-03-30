package com.amolieres.setlistync.feature.band.songs.presentation

sealed interface BandSongsEvent {
    object NavigateBack : BandSongsEvent
}
