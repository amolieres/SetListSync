package com.amolieres.setlistync.feature.band.presentation

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role

data class BandDetailUiState(
    val isLoading: Boolean = true,
    val band: Band? = null,
    val error: String? = null,
    // Member dialog
    val showMemberDialog: Boolean = false,
    val editingMember: BandMember? = null,
    val memberNickname: String = "",
    val memberRoles: Set<Role> = emptySet(),
    // Delete band confirmation
    val showDeleteBandConfirm: Boolean = false
)
