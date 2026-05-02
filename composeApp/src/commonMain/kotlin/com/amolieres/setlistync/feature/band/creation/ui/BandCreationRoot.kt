package com.amolieres.setlistync.feature.band.creation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BandCreationRoot(
    onNavigateBack: () -> Unit,
    onNavigateToBandDetail: (String) -> Unit
) {
    val viewModel: BandCreationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    BandCreationScreen(
        uiState = uiState,
        eventsFlow = viewModel.events,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToBandDetail = onNavigateToBandDetail
    )
}
