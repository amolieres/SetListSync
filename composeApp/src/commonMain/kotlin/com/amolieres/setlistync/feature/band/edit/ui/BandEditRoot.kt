package com.amolieres.setlistync.feature.band.edit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BandEditRoot(
    onNavigateBack: () -> Unit
) {
    val viewModel: BandEditViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    BandEditScreen(
        uiState = uiState,
        eventsFlow = viewModel.events,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack
    )
}
