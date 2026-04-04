package com.amolieres.setlistync.feature.band.gigs.presentation

import com.amolieres.setlistync.core.domain.band.model.Gig

data class BandGigsUiState(
    val isLoading: Boolean = true,
    val bandName: String = "",
    val gigs: List<Gig> = emptyList()
)
