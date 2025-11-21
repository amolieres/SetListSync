package com.amolieres.setlistync.feature.user.presentation

import com.amolieres.setlistync.core.domain.user.model.User

sealed interface UserAuthEvent {
    data class OnSubmitSuccess(val user: User) : UserAuthEvent
}