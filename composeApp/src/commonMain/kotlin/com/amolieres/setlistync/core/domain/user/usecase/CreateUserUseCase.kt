package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.data.preferences.UserPreferencesRepository
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class CreateUserUseCase(private val repo: UserRepository, private val prefs: UserPreferencesRepository) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<User> = runCatching {
        val trimmedEmail = email.trim()
        val existingUser = repo.getUserByEmail(email)
        if (existingUser != null) {
            throw Exception("This email is already registered.")
        }

        //TODO: Need to hash password ASAP !!!
        val newUser = User(
            id = Uuid.random().toString(),
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = trimmedEmail,
            passwordHash = password
        )

        repo.createUser(newUser)
        prefs.saveUser(newUser.id, newUser.passwordHash ?: "")
        newUser
    }
}