package com.amolieres.setlistync.feature.band.members.presentation

import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role

data class BandMembersUiState(
    val isLoading: Boolean = true,
    val bandName: String = "",
    val members: List<BandMember> = emptyList(),
    // Member dialog
    val showMemberDialog: Boolean = false,
    val editingMember: BandMember? = null,
    val memberNickname: String = "",
    val memberRoles: Set<Role> = emptySet()
)
