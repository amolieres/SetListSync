package com.amolieres.setlistync.core.domain.model

data class Song(
    val id: SongId,
    val title: String,
    val durationSeconds: Int,
    val key: String? = null,
    val externalLinks: List<String> = emptyList()
)