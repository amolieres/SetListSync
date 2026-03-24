package com.amolieres.setlistync.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "song_notes")
data class SongNoteEntity(
    @PrimaryKey val id: String,
    val songId: String,    // SongId.value
    val memberId: String,
    val note: String
)
