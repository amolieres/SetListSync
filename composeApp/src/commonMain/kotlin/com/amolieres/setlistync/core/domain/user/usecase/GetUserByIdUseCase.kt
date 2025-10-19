package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository

class GetUserByIdUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(id: String): User? = repo.getUserById(id)
}