package com.amolieres.setlistync.feature.main.presentation

sealed interface MainEvent {
    object NavigateToLogin : MainEvent
    data class NavigateToBandDetail(val bandId: String) : MainEvent
}