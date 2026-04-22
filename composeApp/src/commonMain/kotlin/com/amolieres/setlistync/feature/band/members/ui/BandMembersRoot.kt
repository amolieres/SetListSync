package com.amolieres.setlistync.feature.band.members.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BandMembersRoot(
    onNavigateBack: () -> Unit
) {
    val viewModel: BandMembersViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    BandMembersScreen(
        uiState = uiState,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack
    )
}
