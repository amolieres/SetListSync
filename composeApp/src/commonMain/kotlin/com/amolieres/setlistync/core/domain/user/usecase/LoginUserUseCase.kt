package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository

class LoginUserUseCase (private val repo: UserRepository, private val prefs: UserPreferencesRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> = runCatching {

        val existingUser = repo.getUserByEmail(email)
        checkNotNull(existingUser, { "User not found. Please create an account." })

       if (existingUser.passwordHash != password) {
           throw Exception("Incorrect password.")
        }

        prefs.saveUser(existingUser.id, existingUser.passwordHash)

        existingUser
    }
}