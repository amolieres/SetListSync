package com.amolieres.setlistync.feature.band.edit.presentation

data class BandEditUiState(
    val isLoading: Boolean = true,
    val bandNotFound: Boolean = false,
    val name: String = "",
    val email: String = "",
    val instagramUrl: String = "",
    val facebookUrl: String = "",
    val tiktokUrl: String = "",
    val genres: List<String> = emptyList(),
    val genreInput: String = "",
    val isSaving: Boolean = false
)
