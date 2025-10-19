package com.amolieres.setlistync.core.domain.band.model

import kotlinx.serialization.Serializable

@Serializable
data class BandMember(
    val id : String,
    val userId: String?,        // reference to User.id
    val roles: List<Role> = emptyList(),
    val nickname: String? = null // optional band-specific nickname
)