package com.amolieres.setlistync.feature.main.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.main.presentation.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainRoot(
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBandDetail: (String) -> Unit,
    onNavigateToBandCreation: () -> Unit
) {
    val viewModel: MainViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    MainScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToBandDetail = onNavigateToBandDetail,
        onNavigateToBandCreation = onNavigateToBandCreation
    )
}
