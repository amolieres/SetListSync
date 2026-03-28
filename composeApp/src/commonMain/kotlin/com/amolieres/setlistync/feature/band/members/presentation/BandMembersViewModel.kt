package com.amolieres.setlistync.feature.band.members.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import com.amolieres.setlistync.core.domain.band.usecase.AddMemberToBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.RemoveMemberFromBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateMemberInBandUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BandMembersViewModel(
    savedStateHandle: SavedStateHandle,
    observeBand: ObserveBandUseCase,
    private val addMember: AddMemberToBandUseCase,
    private val removeMember: RemoveMemberFromBandUseCase,
    private val updateMember: UpdateMemberInBandUseCase
) : ViewModel() {

    private val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private data class ViewState(
        val showMemberDialog: Boolean = false,
        val editingMember: BandMember? = null,
        val memberNickname: String = "",
        val memberRoles: Set<Role> = emptySet()
    )

    private val _viewState = MutableStateFlow(ViewState())

    val uiState: StateFlow<BandMembersUiState> = combine(
        observeBand(bandId),
        _viewState
    ) { band, view ->
        BandMembersUiState(
            isLoading = false,
            bandName = band?.name ?: "",
            members = band?.members ?: emptyList(),
            showMemberDialog = view.showMemberDialog,
            editingMember = view.editingMember,
            memberNickname = view.memberNickname,
            memberRoles = view.memberRoles
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandMembersUiState())

    fun onScreenEvent(event: BandMembersUiEvent) {
        when (event) {
            BandMembersUiEvent.OnAddMemberClicked -> _viewState.update {
                it.copy(showMemberDialog = true, editingMember = null, memberNickname = "", memberRoles = emptySet())
            }
            is BandMembersUiEvent.OnEditMemberClicked -> _viewState.update {
                it.copy(
                    showMemberDialog = true,
                    editingMember = event.member,
                    memberNickname = event.member.nickname ?: "",
                    memberRoles = event.member.roles.toSet()
                )
            }
            is BandMembersUiEvent.OnDeleteMemberClicked -> deleteMember(event.memberId)
            BandMembersUiEvent.OnMemberDialogDismiss -> dismissDialog()
            is BandMembersUiEvent.OnMemberNicknameChanged -> _viewState.update { it.copy(memberNickname = event.nickname) }
            is BandMembersUiEvent.OnMemberRoleToggled -> toggleRole(event.role)
            BandMembersUiEvent.OnMemberDialogConfirmed -> saveMember()
        }
    }

    private fun toggleRole(role: Role) {
        _viewState.update { state ->
            val roles = state.memberRoles.toMutableSet()
            if (role in roles) roles.remove(role) else roles.add(role)
            state.copy(memberRoles = roles)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveMember() {
        val view = _viewState.value
        val nickname = view.memberNickname.trim().ifBlank { null }
        val roles = view.memberRoles.toList()

        viewModelScope.launch {
            val editing = view.editingMember
            if (editing != null) {
                updateMember(bandId, editing.copy(nickname = nickname, roles = roles))
            } else {
                addMember(
                    bandId, BandMember(
                        id = Uuid.random().toString(),
                        userId = null,
                        nickname = nickname,
                        roles = roles
                    )
                )
            }
            dismissDialog()
        }
    }

    private fun deleteMember(memberId: String) {
        viewModelScope.launch {
            removeMember(bandId, memberId)
        }
    }

    private fun dismissDialog() {
        _viewState.update {
            it.copy(showMemberDialog = false, editingMember = null, memberNickname = "", memberRoles = emptySet())
        }
    }
}
