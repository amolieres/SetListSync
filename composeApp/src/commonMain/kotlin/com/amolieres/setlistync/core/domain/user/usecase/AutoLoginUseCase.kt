package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

/**
 * Attempts to restore a user session from stored credentials.
 * Returns the logged-in [User] if a valid session exists, null otherwise.
 * Clears stale credentials if the user no longer exists or the password no longer matches.
 */
class AutoLoginUseCase(
    private val prefs: UserPreferencesRepository,
    private val userRepo: UserRepository
) {
    suspend operator fun invoke(): User? {
        val userId = prefs.userId.firstOrNull() ?: return null
        val storedPassword = prefs.userPassword.firstOrNull() ?: return null

        val user = userRepo.getUserById(userId) ?: run {
            prefs.clearUser()
            return null
        }

        if (user.passwordHash != storedPassword) {
            prefs.clearUser()
            return null
        }

        return user
    }
}
