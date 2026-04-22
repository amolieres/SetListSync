package com.amolieres.setlistync.feature.band.detail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BandDetailRoot(
    onNavigateBack: () -> Unit,
    onNavigateToMembers: (String) -> Unit,
    onNavigateToSongs: (String) -> Unit,
    onNavigateToNewGig: (String) -> Unit,
    onNavigateToGigDetail: (bandId: String, gigId: String) -> Unit,
    onNavigateToEdit: (String) -> Unit
) {
    val viewModel: BandDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    BandDetailScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToMembers = { onNavigateToMembers(viewModel.bandId) },
        onNavigateToSongs = { onNavigateToSongs(viewModel.bandId) },
        onNavigateToNewGig = { onNavigateToNewGig(viewModel.bandId) },
        onNavigateToGigDetail = { gigId -> onNavigateToGigDetail(viewModel.bandId, gigId) },
        onNavigateToEdit = { onNavigateToEdit(viewModel.bandId) }
    )
}
