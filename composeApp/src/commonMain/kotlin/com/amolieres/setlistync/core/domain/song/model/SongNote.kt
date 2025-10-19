package com.amolieres.setlistync.core.domain.song.model

import kotlinx.serialization.Serializable

@Serializable
data class SongNote(
    val id: String,
    val songId: SongId,
    val memberId: String,
    val note: String
)