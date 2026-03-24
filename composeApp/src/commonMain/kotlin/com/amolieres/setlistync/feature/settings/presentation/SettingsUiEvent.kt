package com.amolieres.setlistync.feature.settings.presentation

import com.amolieres.setlistync.core.data.preferences.NoteNotation

sealed interface SettingsUiEvent {
    data class NotationChanged(val notation: NoteNotation) : SettingsUiEvent
    // Dialogs
    object OnLogoutClicked : SettingsUiEvent
    object OnDeleteAccountClicked : SettingsUiEvent
    object OnLogoutConfirmed : SettingsUiEvent
    object OnDeleteConfirmed : SettingsUiEvent
    object OnDialogDismiss : SettingsUiEvent
}