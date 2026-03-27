package com.amolieres.setlistync.feature.band.presentation

import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role

sealed interface BandDetailUiEvent {
    // Member dialog
    object OnAddMemberClicked : BandDetailUiEvent
    data class OnEditMemberClicked(val member: BandMember) : BandDetailUiEvent
    data class OnDeleteMemberClicked(val memberId: String) : BandDetailUiEvent
    object OnMemberDialogDismiss : BandDetailUiEvent
    data class OnMemberNicknameChanged(val nickname: String) : BandDetailUiEvent
    data class OnMemberRoleToggled(val role: Role) : BandDetailUiEvent
    object OnMemberDialogConfirmed : BandDetailUiEvent
    // Delete band
    object OnDeleteBandClicked : BandDetailUiEvent
    object OnDeleteBandConfirmed : BandDetailUiEvent
    object OnDeleteBandDismiss : BandDetailUiEvent
}
