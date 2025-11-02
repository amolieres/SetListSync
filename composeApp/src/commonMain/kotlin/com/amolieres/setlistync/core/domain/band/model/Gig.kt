package com.amolieres.setlistync.core.domain.band.model

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Gig @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val bandId: String,
    val venue: String? = null,
    val date: Instant? = null,
    val expectedDurationMinutes: Int? = null,
    val setListIds: List<String>? = null
)