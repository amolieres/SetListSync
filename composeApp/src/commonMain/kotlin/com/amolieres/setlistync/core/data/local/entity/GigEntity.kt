package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [dateEpochMs] stores [kotlin.time.Instant.epochMilliseconds], or null if no date set.
 * [sets] is a JSON-encoded List<GigSet>.
 */
@Entity(tableName = "gigs")
data class GigEntity(
    @PrimaryKey val id: String,
    val bandId: String,
    val venue: String?,
    val dateEpochMs: Long?,
    val expectedDurationMinutes: Int?,
    val sets: String  // JSON-encoded List<GigSet>
)
