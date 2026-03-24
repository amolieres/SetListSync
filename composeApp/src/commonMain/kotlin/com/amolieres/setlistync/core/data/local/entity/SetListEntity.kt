package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [orderedSongIds] is stored as a JSON-encoded List<String> (SongId.value) via [Converters].
 * Order in the list defines the setlist order.
 */
@Entity(tableName = "set_lists")
data class SetListEntity(
    @PrimaryKey val id: String,
    val name: String,
    val orderedSongIds: String  // JSON-encoded List<String>
)
