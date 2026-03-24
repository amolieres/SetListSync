package com.amolieres.setlistync.feature.settings.presentation

sealed interface SettingsEvent {
    object NavigateToLogin : SettingsEvent
}