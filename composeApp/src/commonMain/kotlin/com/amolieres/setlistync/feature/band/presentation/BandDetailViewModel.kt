package com.amolieres.setlistync.feature.band.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import com.amolieres.setlistync.core.domain.band.usecase.AddMemberToBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.DeleteBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.RemoveMemberFromBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateMemberInBandUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BandDetailViewModel(
    savedStateHandle: SavedStateHandle,
    observeBand: ObserveBandUseCase,
    private val addMember: AddMemberToBandUseCase,
    private val removeMember: RemoveMemberFromBandUseCase,
    private val updateMember: UpdateMemberInBandUseCase,
    private val deleteBand: DeleteBandUseCase
) : ViewModel() {

    private val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _event = MutableSharedFlow<BandDetailEvent>()
    val event: SharedFlow<BandDetailEvent> = _event.asSharedFlow()

    private data class ViewState(
        val showMemberDialog: Boolean = false,
        val editingMember: BandMember? = null,
        val memberNickname: String = "",
        val memberRoles: Set<Role> = emptySet(),
        val showDeleteBandConfirm: Boolean = false
    )

    private val _uiState = MutableStateFlow(ViewState())

    val uiState: StateFlow<BandDetailUiState> = combine(
        observeBand(bandId),
        _uiState
    ) { band, view ->
        BandDetailUiState(
            isLoading = false,
            band = band,
            showMemberDialog = view.showMemberDialog,
            editingMember = view.editingMember,
            memberNickname = view.memberNickname,
            memberRoles = view.memberRoles,
            showDeleteBandConfirm = view.showDeleteBandConfirm
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BandDetailUiState())

    fun onScreenEvent(event: BandDetailUiEvent) {
        when (event) {
            BandDetailUiEvent.OnAddMemberClicked -> _uiState.update {
                it.copy(showMemberDialog = true, editingMember = null, memberNickname = "", memberRoles = emptySet())
            }
            is BandDetailUiEvent.OnEditMemberClicked -> _uiState.update {
                it.copy(
                    showMemberDialog = true,
                    editingMember = event.member,
                    memberNickname = event.member.nickname ?: "",
                    memberRoles = event.member.roles.toSet()
                )
            }
            is BandDetailUiEvent.OnDeleteMemberClicked -> deleteMember(event.memberId)
            BandDetailUiEvent.OnMemberDialogDismiss -> dismissMemberDialog()
            is BandDetailUiEvent.OnMemberNicknameChanged -> _uiState.update { it.copy(memberNickname = event.nickname) }
            is BandDetailUiEvent.OnMemberRoleToggled -> toggleRole(event.role)
            BandDetailUiEvent.OnMemberDialogConfirmed -> saveMember()
            BandDetailUiEvent.OnDeleteBandClicked -> _uiState.update { it.copy(showDeleteBandConfirm = true) }
            BandDetailUiEvent.OnDeleteBandConfirmed -> doDeleteBand()
            BandDetailUiEvent.OnDeleteBandDismiss -> _uiState.update { it.copy(showDeleteBandConfirm = false) }
        }
    }

    private fun toggleRole(role: Role) {
        _uiState.update { state ->
            val roles = state.memberRoles.toMutableSet()
            if (role in roles) roles.remove(role) else roles.add(role)
            state.copy(memberRoles = roles)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveMember() {
        val view = _uiState.value
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
            dismissMemberDialog()
            // No manual reload — Room Flow re-emits automatically
        }
    }

    private fun deleteMember(memberId: String) {
        viewModelScope.launch {
            removeMember(bandId, memberId)
            // No manual reload — Room Flow re-emits automatically
        }
    }

    private fun doDeleteBand() {
        viewModelScope.launch {
            deleteBand(bandId)
            _event.emit(BandDetailEvent.NavigateBack)
        }
    }

    private fun dismissMemberDialog() {
        _uiState.update {
            it.copy(showMemberDialog = false, editingMember = null, memberNickname = "", memberRoles = emptySet())
        }
    }
}
