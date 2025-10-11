package com.amolieres.setlistync.core.domain.model

data class SetList(
    val id: String,
    val name: String,
    val orderedSongIds: List<SongId> = emptyList()
)