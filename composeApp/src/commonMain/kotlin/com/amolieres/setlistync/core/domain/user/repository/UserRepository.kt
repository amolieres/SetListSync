package com.amolieres.setlistync.core.domain.user.repository

import com.amolieres.setlistync.core.domain.user.model.User

interface UserRepository {
    suspend fun createUser(user: User)
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUser(user: User)
    suspend fun deleteUser(id: String)
    suspend fun getAllUsers(): List<User>
}