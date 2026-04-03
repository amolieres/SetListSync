package com.amolieres.setlistync.feature.band.songDetail.presentation

sealed interface BandSongDetailEvent {
    data object NavigateBack : BandSongDetailEvent
}
