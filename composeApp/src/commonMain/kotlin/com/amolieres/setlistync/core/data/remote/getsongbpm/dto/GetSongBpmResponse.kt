package com.amolieres.setlistync.core.data.remote.getsongbpm.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetSongBpmSearchResponse(
    val search: List<GetSongBpmTrackDto>? = null
)

@Serializable
data class GetSongBpmTrackDto(
    val tempo: String? = null,
    @SerialName("key_of") val keyOf: String? = null
)
