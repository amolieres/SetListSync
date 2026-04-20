package com.amolieres.setlistync.core.domain.band.model

import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlinx.serialization.Serializable

@Serializable
data class GigSet(
    val id: String,
    val title: String? = null,
    val orderedSongIds: List<SongId> = emptyList()
)
