package com.amolieres.setlistync.feature.band.songs.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BandSongsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToNewSong: (String) -> Unit,
    onNavigateToEditSong: (bandId: String, songId: String) -> Unit
) {
    val viewModel: BandSongsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    BandSongsScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToNewSong = { onNavigateToNewSong(viewModel.bandId) },
        onNavigateToEditSong = { songId -> onNavigateToEditSong(viewModel.bandId, songId.value) }
    )
}
