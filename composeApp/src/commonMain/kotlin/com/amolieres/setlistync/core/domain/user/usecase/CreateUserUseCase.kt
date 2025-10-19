package com.amolieres.setlistync.core.domain.user.usecase

import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository


class CreateUserUseCase(private val repo: UserRepository) {
    suspend operator fun invoke(user: User) = repo.createUser(user)
}