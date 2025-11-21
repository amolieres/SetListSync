package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import kotlinx.coroutines.flow.firstOrNull

class DeleteCurrentUserUseCase(
    private val prefs: UserPreferencesRepository,
    private val userRepo: UserRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        val id = prefs.userId.firstOrNull()
        checkNotNull(id, { "current user not fond" })
        userRepo.deleteUser(id)
        prefs.clearUser()
        return Result.success(Unit)
    }
}