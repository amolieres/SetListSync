package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [dateEpochMs] stores [kotlin.time.Instant.epochMilliseconds], or null if no date set.
 * [orderedSongIds] is a JSON-encoded List<String> (SongId values). Order defines the setlist.
 */
@Entity(tableName = "gigs")
data class GigEntity(
    @PrimaryKey val id: String,
    val bandId: String,
    val venue: String?,
    val dateEpochMs: Long?,
    val expectedDurationMinutes: Int?,
    val orderedSongIds: String  // JSON-encoded List<String>
)
