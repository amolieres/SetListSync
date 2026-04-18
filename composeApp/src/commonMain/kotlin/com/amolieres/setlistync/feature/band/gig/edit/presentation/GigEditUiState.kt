package com.amolieres.setlistync.feature.band.gig.edit.presentation

import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.song.model.SongId

data class GigEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val venueInput: String = "",
    val dateMillis: Long? = null,
    val expectedDurationInput: String = "",
    val showDatePicker: Boolean = false,
    val importedSongIds: List<SongId> = emptyList(),
    val showImportSheet: Boolean = false,
    val gigsForImport: List<Gig> = emptyList()
)
