package com.amolieres.setlistync.feature.band.songDetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BandSongDetailRoot(
    onNavigateBack: () -> Unit
) {
    val viewModel: BandSongDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    BandSongDetailScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack
    )
}
