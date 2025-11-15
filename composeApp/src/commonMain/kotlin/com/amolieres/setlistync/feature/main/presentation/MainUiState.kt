package com.amolieres.setlistync.feature.main.presentation

data class MainUiState(
    val contentText: String = "Welcome to SetListSync 🎵",
    val showConfirmDialog: ConfirmDialogType? = null
)

enum class ConfirmDialogType {
    Logout,
    DeleteAccount
}
