package com.amolieres.setlistync.feature.settings.presentation

import com.amolieres.setlistync.core.data.preferences.NoteNotation

data class SettingsUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val userEmail: String = "",
    val notation: NoteNotation = NoteNotation.FR,
    val showConfirmDialog: ConfirmDialogType? = null
)

enum class ConfirmDialogType {
    Logout,
    DeleteAccount
}