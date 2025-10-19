package com.amolieres.setlistync.core.domain.user.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,          // ex: UUID
    val firstName: String,
    val lastName: String,
    val email: String,
    val passwordHash: String? = null // may be null if external auth
) {
    val fullName: String get() = "$firstName $lastName"
}