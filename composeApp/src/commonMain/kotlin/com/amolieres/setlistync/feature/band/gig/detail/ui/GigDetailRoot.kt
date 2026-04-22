package com.amolieres.setlistync.feature.band.gig.detail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GigDetailRoot(
    onNavigateBack: () -> Unit,
    onNavigateToEditGig: (bandId: String, gigId: String) -> Unit
) {
    val viewModel: GigDetailViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    GigDetailScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToEditGig = { onNavigateToEditGig(viewModel.bandId, viewModel.gigId) }
    )
}
