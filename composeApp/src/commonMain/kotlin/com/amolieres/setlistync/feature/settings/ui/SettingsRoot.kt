package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.settings.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsRoot(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    SettingsScreen(
        uiState = uiState,
        eventsFlow = viewModel.events,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateBack = onNavigateBack,
        onNavigateToLogin = onNavigateToLogin
    )
}
