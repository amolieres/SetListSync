package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository

class GetUserByEmailUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(email: String): User? = repo.getUserByEmail(email)
}