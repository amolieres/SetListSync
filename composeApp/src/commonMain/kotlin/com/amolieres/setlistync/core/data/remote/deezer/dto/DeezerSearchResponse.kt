package com.amolieres.setlistync.core.data.remote.deezer.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeezerSearchResponse(val data: List<DeezerTrackDto>)

@Serializable
data class DeezerTrackDto(
    val id: Long,
    val title: String,
    val duration: Int,
    val artist: DeezerArtistDto
)

@Serializable
data class DeezerArtistDto(val name: String)
