package com.amolieres.setlistync.feature.user.presentation

sealed interface UserAuthEvent {
    data class FirstNameChanged(val value: String) : UserAuthEvent
    data class LastNameChanged(val value: String) : UserAuthEvent
    data class EmailChanged(val value: String) : UserAuthEvent
    data class PasswordChanged(val value: String) : UserAuthEvent
    object ToggleMode : UserAuthEvent
    object Submit : UserAuthEvent
}