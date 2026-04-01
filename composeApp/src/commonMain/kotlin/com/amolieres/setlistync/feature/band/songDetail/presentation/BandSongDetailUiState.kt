package com.amolieres.setlistync.feature.band.songDetail.presentation

data class BandSongDetailUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val minutes: String = "",
    val seconds: String = "",
    val key: String = "",
    val tempo: String = "",
    val originalArtist: String = "",
    val isSaving: Boolean = false
)
