package com.amolieres.setlistync.feature.band.edit.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amolieres.setlistync.core.domain.band.usecase.GetBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateBandUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BandEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val getBand: GetBandUseCase,
    private val updateBand: UpdateBandUseCase
) : ViewModel() {

    private val bandId: String = checkNotNull(savedStateHandle.get<String>("bandId"))

    private val _uiState = MutableStateFlow(BandEditUiState())
    val uiState: StateFlow<BandEditUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BandEditEvent>()
    val events: SharedFlow<BandEditEvent> = _events.asSharedFlow()

    init {
        loadBand()
    }

    fun onScreenEvent(event: BandEditUiEvent) {
        when (event) {
            is BandEditUiEvent.OnNameChanged ->
                _uiState.update { it.copy(name = event.value) }
            is BandEditUiEvent.OnEmailChanged ->
                _uiState.update { it.copy(email = event.value) }
            is BandEditUiEvent.OnInstagramChanged ->
                _uiState.update { it.copy(instagramUrl = event.value) }
            is BandEditUiEvent.OnFacebookChanged ->
                _uiState.update { it.copy(facebookUrl = event.value) }
            is BandEditUiEvent.OnTiktokChanged ->
                _uiState.update { it.copy(tiktokUrl = event.value) }
            is BandEditUiEvent.OnGenreInputChanged ->
                _uiState.update { it.copy(genreInput = event.value) }
            BandEditUiEvent.OnAddGenreClicked -> addGenre()
            is BandEditUiEvent.OnRemoveGenre ->
                _uiState.update { it.copy(genres = it.genres - event.genre) }
            BandEditUiEvent.OnSaveClicked -> save()
            BandEditUiEvent.OnBackClicked ->
                viewModelScope.launch { _events.emit(BandEditEvent.NavigateBack) }
        }
    }

    private fun loadBand() {
        viewModelScope.launch {
            val band = getBand(bandId)
            if (band == null) {
                _uiState.update { it.copy(isLoading = false, bandNotFound = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = band.name,
                        email = band.email ?: "",
                        instagramUrl = band.instagramUrl ?: "",
                        facebookUrl = band.facebookUrl ?: "",
                        tiktokUrl = band.tiktokUrl ?: "",
                        genres = band.genres
                    )
                }
            }
        }
    }

    private fun addGenre() {
        val input = _uiState.value.genreInput.trim()
        if (input.isBlank()) return
        _uiState.update { it.copy(genres = it.genres + input, genreInput = "") }
    }

    private fun save() {
        val state = _uiState.value
        if (state.name.isBlank()) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val current = getBand(bandId) ?: return@launch
            updateBand(
                current.copy(
                    name = state.name.trim(),
                    email = state.email.trim().ifBlank { null },
                    instagramUrl = state.instagramUrl.trim().ifBlank { null },
                    facebookUrl = state.facebookUrl.trim().ifBlank { null },
                    tiktokUrl = state.tiktokUrl.trim().ifBlank { null },
                    genres = state.genres
                )
            )
            _uiState.update { it.copy(isSaving = false) }
            _events.emit(BandEditEvent.NavigateBack)
        }
    }
}
