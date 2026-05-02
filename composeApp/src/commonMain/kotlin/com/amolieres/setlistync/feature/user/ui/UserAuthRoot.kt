package com.amolieres.setlistync.feature.user.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.amolieres.setlistync.feature.user.presentation.UserAuthViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UserAuthRoot(
    onNavigateToMain: () -> Unit
) {
    val viewModel: UserAuthViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    UserAuthScreen(
        uiState = uiState,
        eventFlow = viewModel.event,
        onScreenEvent = viewModel::onScreenEvent,
        onNavigateToMain = onNavigateToMain
    )
}
