package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [externalLinks] is stored as a JSON-encoded List<String> via [Converters].
 */
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: String,
    val title: String,
    val durationSeconds: Int,
    val key: String?,
    val tempo: Int?,
    val externalLinks: String  // JSON-encoded List<String>
)
