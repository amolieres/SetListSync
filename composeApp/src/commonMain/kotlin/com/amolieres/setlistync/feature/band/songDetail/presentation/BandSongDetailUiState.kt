package com.amolieres.setlistync.feature.band.songDetail.presentation

import com.amolieres.setlistync.core.domain.song.model.SongSearchResult

data class BandSongDetailUiState(
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val title: String = "",
    val minutes: String = "",
    val seconds: String = "",
    val key: String = "",
    val tempo: String = "",
    val originalArtist: String = "",
    val isSaving: Boolean = false,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<SongSearchResult> = emptyList(),
    val isLoadingFeatures: Boolean = false
)
