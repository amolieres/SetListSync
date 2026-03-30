package com.amolieres.setlistync.feature.main.presentation

import com.amolieres.setlistync.core.domain.band.model.Band

data class MainUiState(
    val isLoading: Boolean = true,
    val bands: List<Band> = emptyList(),
    val error: String? = null
)
