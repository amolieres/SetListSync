package com.amolieres.setlistync.feature.band.songDetail.presentation

import kotlinx.serialization.Serializable

@Serializable data class BandSongDetailRoute(val bandId: String, val songId: String? = null)
