package com.amolieres.setlistync.feature.band.gig.edit.presentation

data class GigEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val venueInput: String = "",
    val dateMillis: Long? = null,
    val expectedDurationInput: String = "",
    val showDatePicker: Boolean = false
)
