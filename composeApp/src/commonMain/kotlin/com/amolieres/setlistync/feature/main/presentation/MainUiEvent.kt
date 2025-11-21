package com.amolieres.setlistync.feature.main.presentation

sealed interface MainUiEvent {
    object OnSettingsClicked : MainUiEvent
    object OnSettingsDismiss : MainUiEvent
}