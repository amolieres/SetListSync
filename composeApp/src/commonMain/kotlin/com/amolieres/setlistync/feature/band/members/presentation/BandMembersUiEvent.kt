package com.amolieres.setlistync.feature.band.members.presentation

import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role

sealed interface BandMembersUiEvent {
    object OnAddMemberClicked : BandMembersUiEvent
    data class OnEditMemberClicked(val member: BandMember) : BandMembersUiEvent
    data class OnDeleteMemberClicked(val memberId: String) : BandMembersUiEvent
    object OnMemberDialogDismiss : BandMembersUiEvent
    data class OnMemberNicknameChanged(val nickname: String) : BandMembersUiEvent
    data class OnMemberRoleToggled(val role: Role) : BandMembersUiEvent
    object OnMemberDialogConfirmed : BandMembersUiEvent
}
