package com.amolieres.setlistync.feature.main.presentation

sealed interface MainEvent {
    object OnLogoutClicked : MainEvent
    object OnDeleteAccountClicked : MainEvent
    object OnLogoutConfirmed : MainEvent
    object OnDeleteConfirmed : MainEvent

    object OnDialogDismiss : MainEvent
}