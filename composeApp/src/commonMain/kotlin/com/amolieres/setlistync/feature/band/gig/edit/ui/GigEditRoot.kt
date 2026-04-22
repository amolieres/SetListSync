package com.amolieres.setlistync.feature.band.gig.edit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GigEditRoot(
    onNavigateBack: () -> Unit,
    onNavigateToGigDetail: (bandId: String, gigId: String) -> Unit
) {
    val viewModel: GigEditViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    GigEditScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToGigDetail = { gigId -> onNavigateToGigDetail(viewModel.bandId, gigId) },
        isEditMode = viewModel.isEditMode
    )
}
