package com.amolieres.setlistync.feature.band.creation.presentation

import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role

data class BandCreationUiState(
    val currentStep: Int = 1,
    // Step 1
    val bandName: String = "",
    val genres: List<String> = emptyList(),
    val genreInput: String = "",
    // Step 2
    val email: String = "",
    val instagramUrl: String = "",
    val facebookUrl: String = "",
    val tiktokUrl: String = "",
    // Step 3 — members kept in memory (not yet persisted)
    val members: List<BandMember> = emptyList(),
    val showMemberDialog: Boolean = false,
    val editingMember: BandMember? = null,
    val memberNickname: String = "",
    val memberRoles: Set<Role> = emptySet(),
    val isLoading: Boolean = false
)
