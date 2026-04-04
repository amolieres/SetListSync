package com.amolieres.setlistync.feature.band.gigDetail.presentation

sealed interface GigDetailEvent {
    data object NavigateBack : GigDetailEvent
}
