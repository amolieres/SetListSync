package com.amolieres.setlistync.core.data.user

import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UserRepositoryLocal : UserRepository {
    private val mutex = Mutex()
    private val storage = mutableMapOf<String, User>()
    override suspend fun createUser(user: User) = mutex.withLock {
        storage[user.id] = user
    }

    override suspend fun getUserById(id: String): User? = mutex.withLock {
        storage[id]
    }

    override suspend fun getUserByEmail(email: String): User? = mutex.withLock {
        storage.values.firstOrNull { it.email.equals(email, ignoreCase = true) }
    }

    override suspend fun updateUser(user: User) = mutex.withLock {
        if (!storage.containsKey(user.id)) throw IllegalArgumentException("User not found: ${user.id}")
        storage[user.id] = user
    }

    override suspend fun deleteUser(id: String): Unit = mutex.withLock {
        storage.remove(id)
    }

    override suspend fun getAllUsers(): List<User> = mutex.withLock {
        storage.values.toList()
    }
}
