package com.amolieres.setlistync.feature.band.creation.presentation

import com.amolieres.setlistync.core.domain.band.model.Role

sealed interface BandCreationUiEvent {
    // Navigation between steps
    object OnNextClicked : BandCreationUiEvent
    object OnBackClicked : BandCreationUiEvent

    // Step 1
    data class OnBandNameChanged(val name: String) : BandCreationUiEvent
    data class OnGenreInputChanged(val input: String) : BandCreationUiEvent
    object OnAddGenreClicked : BandCreationUiEvent
    data class OnRemoveGenre(val genre: String) : BandCreationUiEvent

    // Step 2
    data class OnEmailChanged(val email: String) : BandCreationUiEvent
    data class OnInstagramUrlChanged(val url: String) : BandCreationUiEvent
    data class OnFacebookUrlChanged(val url: String) : BandCreationUiEvent
    data class OnTiktokUrlChanged(val url: String) : BandCreationUiEvent

    // Step 3 — member management (in-memory)
    object OnAddMemberClicked : BandCreationUiEvent
    object OnMemberDialogDismiss : BandCreationUiEvent
    object OnMemberDialogConfirm : BandCreationUiEvent
    data class OnMemberNicknameChanged(val nickname: String) : BandCreationUiEvent
    data class OnMemberRoleToggled(val role: Role) : BandCreationUiEvent
}
