package com.amolieres.setlistync.feature.main.presentation

sealed interface MainUiEvent {
    object OnSettingsClicked : MainUiEvent
    object OnSettingsDismiss : MainUiEvent
    object OnCreateBandClicked : MainUiEvent
    data class OnBandClicked(val bandId: String) : MainUiEvent
}
