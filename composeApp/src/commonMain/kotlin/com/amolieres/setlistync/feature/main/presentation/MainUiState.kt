package com.amolieres.setlistync.feature.main.presentation

import com.amolieres.setlistync.core.domain.band.model.BandPresenter

data class MainUiState(
    val isLoading: Boolean = true,
    val bands: List<BandPresenter> = emptyList(),
    val error: String? = null
)
