package com.amolieres.setlistync.feature.main.presentation

sealed interface MainUiEvent {
    object OnSettingsClicked : MainUiEvent
    object OnSettingsDismiss : MainUiEvent
    object OnCreateBandClicked : MainUiEvent
    object OnCreateBandDismiss : MainUiEvent
    data class OnCreateBandNameChanged(val name: String) : MainUiEvent
    object OnCreateBandConfirmed : MainUiEvent
    data class OnBandClicked(val bandId: String) : MainUiEvent
}