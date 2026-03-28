package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bands")
data class BandEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String? = null,
    val instagramUrl: String? = null,
    val facebookUrl: String? = null,
    val tiktokUrl: String? = null,
    val genres: String = "[]"  // JSON-encoded List<String>
)
