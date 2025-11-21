package com.amolieres.setlistync.feature.user.presentation

sealed interface UserAuthUiEvent {
    data class FirstNameChanged(val value: String) : UserAuthUiEvent
    data class LastNameChanged(val value: String) : UserAuthUiEvent
    data class EmailChanged(val value: String) : UserAuthUiEvent
    data class PasswordChanged(val value: String) : UserAuthUiEvent
    object ToggleMode : UserAuthUiEvent
    object Submit : UserAuthUiEvent
}