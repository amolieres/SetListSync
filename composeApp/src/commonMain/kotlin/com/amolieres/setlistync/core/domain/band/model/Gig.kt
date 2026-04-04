package com.amolieres.setlistync.core.domain.band.model

import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Gig @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val bandId: String,
    val venue: String? = null,
    @Contextual val date: Instant? = null,
    val expectedDurationMinutes: Int? = null,
    val orderedSongIds: List<SongId> = emptyList()
)
