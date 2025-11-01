package com.amolieres.setlistync.core.data.user

import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository

class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()

    override suspend fun createUser(user: User) {
        users.add(user)
    }

    override suspend fun getUserById(id: String): User? = users.firstOrNull { it.id == id }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email.equals(email, ignoreCase = true) }
    }

    override suspend fun updateUser(user: User) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: String) {
        getUserById(id)?.let {
            users.remove(it)
        }
    }

    override suspend fun getAllUsers(): List<User> = users.toList()


}