package com.amolieres.setlistync.core.domain.setList.model

import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlinx.serialization.Serializable

@Serializable
data class SetList(
    val id: String,
    val name: String,
    val orderedSongIds: List<SongId> = emptyList()
)