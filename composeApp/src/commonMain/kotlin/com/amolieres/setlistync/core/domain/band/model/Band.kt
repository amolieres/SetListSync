package com.amolieres.setlistync.core.domain.band.model

import com.amolieres.setlistync.core.domain.band.model.Gig
import kotlinx.serialization.Serializable

@Serializable
data class Band(
    val id: String,
    val name: String,
    val members: List<BandMember> = emptyList(),
    val gigs: List<Gig> = emptyList()
)