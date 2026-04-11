package com.amolieres.setlistync.feature.band.detail.presentation

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.Gig

data class BandDetailUiState(
    val isLoading: Boolean = true,
    val band: Band? = null,
    val error: String? = null,
    val showDeleteBandConfirm: Boolean = false,
    val songCount: Int = 0,
    val gigs: List<Gig> = emptyList()
)
