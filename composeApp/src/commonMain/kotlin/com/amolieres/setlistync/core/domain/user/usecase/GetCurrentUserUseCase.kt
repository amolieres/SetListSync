package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

class GetCurrentUserUseCase(
    private val prefs: UserPreferencesRepository,
    private val userRepo: UserRepository
) {
    suspend operator fun invoke(): User? {
        val userId = prefs.userId.firstOrNull() ?: return null
        return userRepo.getUserById(userId)
    }
}