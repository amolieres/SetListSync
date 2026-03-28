package com.amolieres.setlistync.feature.band.creation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.usecase.AddMemberToBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.CreateBandUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BandCreationViewModel(
    private val createBand: CreateBandUseCase,
    private val addMemberToBand: AddMemberToBandUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BandCreationUiState())
    val uiState: StateFlow<BandCreationUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BandCreationEvent>()
    val events: SharedFlow<BandCreationEvent> = _events.asSharedFlow()

    fun onScreenEvent(event: BandCreationUiEvent) {
        when (event) {
            BandCreationUiEvent.OnBackClicked -> handleBack()
            BandCreationUiEvent.OnNextClicked -> handleNext()

            // Step 1
            is BandCreationUiEvent.OnBandNameChanged ->
                _uiState.update { it.copy(bandName = event.name) }
            is BandCreationUiEvent.OnGenreInputChanged ->
                _uiState.update { it.copy(genreInput = event.input) }
            BandCreationUiEvent.OnAddGenreClicked -> addGenre()
            is BandCreationUiEvent.OnRemoveGenre ->
                _uiState.update { it.copy(genres = it.genres - event.genre) }

            // Step 2
            is BandCreationUiEvent.OnEmailChanged ->
                _uiState.update { it.copy(email = event.email) }
            is BandCreationUiEvent.OnInstagramUrlChanged ->
                _uiState.update { it.copy(instagramUrl = event.url) }
            is BandCreationUiEvent.OnFacebookUrlChanged ->
                _uiState.update { it.copy(facebookUrl = event.url) }
            is BandCreationUiEvent.OnTiktokUrlChanged ->
                _uiState.update { it.copy(tiktokUrl = event.url) }

            // Step 3 member management
            BandCreationUiEvent.OnAddMemberClicked ->
                _uiState.update { it.copy(showMemberDialog = true, memberNickname = "", memberRoles = emptySet(), editingMember = null) }
            BandCreationUiEvent.OnMemberDialogDismiss ->
                _uiState.update { it.copy(showMemberDialog = false) }
            BandCreationUiEvent.OnMemberDialogConfirm -> confirmMember()
            is BandCreationUiEvent.OnMemberNicknameChanged ->
                _uiState.update { it.copy(memberNickname = event.nickname) }
            is BandCreationUiEvent.OnMemberRoleToggled -> {
                _uiState.update {
                    val roles = it.memberRoles.toMutableSet()
                    if (event.role in roles) roles.remove(event.role) else roles.add(event.role)
                    it.copy(memberRoles = roles)
                }
            }
        }
    }

    private fun handleBack() {
        val step = _uiState.value.currentStep
        if (step == 1) {
            emit(BandCreationEvent.NavigateBack)
        } else {
            _uiState.update { it.copy(currentStep = step - 1) }
        }
    }

    private fun handleNext() {
        val state = _uiState.value
        when (state.currentStep) {
            1, 2 -> _uiState.update { it.copy(currentStep = it.currentStep + 1) }
            3 -> createBandAndNavigate()
        }
    }

    private fun addGenre() {
        val input = _uiState.value.genreInput.trim()
        if (input.isBlank()) return
        _uiState.update { it.copy(genres = it.genres + input, genreInput = "") }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun confirmMember() {
        val state = _uiState.value
        val member = BandMember(
            id = state.editingMember?.id ?: Uuid.random().toString(),
            userId = null,
            nickname = state.memberNickname.trim().ifBlank { null },
            roles = state.memberRoles.toList()
        )
        val updatedMembers = if (state.editingMember != null) {
            state.members.map { if (it.id == member.id) member else it }
        } else {
            state.members + member
        }
        _uiState.update { it.copy(members = updatedMembers, showMemberDialog = false) }
    }

    private fun createBandAndNavigate() {
        val state = _uiState.value
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val band = createBand(
                name = state.bandName.trim(),
                genres = state.genres,
                email = state.email.trim().ifBlank { null },
                instagramUrl = state.instagramUrl.trim().ifBlank { null },
                facebookUrl = state.facebookUrl.trim().ifBlank { null },
                tiktokUrl = state.tiktokUrl.trim().ifBlank { null }
            )
            state.members.forEach { member ->
                addMemberToBand(band.id, member)
            }
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(BandCreationEvent.NavigateToBandDetail(band.id))
        }
    }

    private fun emit(event: BandCreationEvent) {
        viewModelScope.launch { _events.emit(event) }
    }
}
