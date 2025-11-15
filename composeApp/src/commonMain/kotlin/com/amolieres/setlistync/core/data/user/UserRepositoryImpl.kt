package com.amolieres.setlistync.core.data.user

import com.amolieres.setlistync.core.data.local.dao.UserDao
import com.amolieres.setlistync.core.data.local.entity.UserEntity
import com.amolieres.setlistync.core.domain.user.model.User
import com.amolieres.setlistync.core.domain.user.repository.UserRepository

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun createUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    override suspend fun getUserById(id: String): User? {
        return userDao.getUserById(id)?.toDomain()
    }

    override suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)?.toDomain()
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(id: String) {
        userDao.deleteUser(id)
    }

    override suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers().map { it.toDomain() }
    }

    private fun User.toEntity() = UserEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        passwordHash = passwordHash
    )

    private fun UserEntity.toDomain() = User(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        passwordHash = passwordHash
    )
}