package com.amolieres.setlistync.core.domain.song.model

import kotlinx.serialization.Serializable

@Serializable
data class Song(
    val id: SongId,
    val title: String,
    val durationSeconds: Int,
    val key: String? = null,
    val tempo: Int? = null,
    val externalLinks: List<String> = emptyList()
)