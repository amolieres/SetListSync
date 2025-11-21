package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository

class LogoutUserUseCase(
    private val prefs: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        prefs.clearUser()
    }
}