package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Flat representation of a BandMember.
 * [roles] is stored as a JSON-encoded List<String> via [Converters].
 */
@Entity(tableName = "band_members")
data class BandMemberEntity(
    @PrimaryKey val id: String,
    val bandId: String,
    val userId: String?,
    val nickname: String?,
    val roles: String  // JSON-encoded List<Role> e.g. ["VOCALS","GUITAR"]
)
