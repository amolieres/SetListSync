package com.amolieres.setlistync.core.domain.model

data class SongNote(
    val id: String,
    val songId: SongId,
    val memberId: String,
    val note: String
)