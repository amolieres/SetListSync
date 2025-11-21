package com.amolieres.setlistync.feature.main.ui

import com.amolieres.setlistync.feature.main.presentation.MainUiEvent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.feature.main.presentation.MainEvent
import com.amolieres.setlistync.feature.main.presentation.MainUiState
import com.amolieres.setlistync.feature.settings.presentation.SettingsViewModel
import com.amolieres.setlistync.feature.settings.ui.SettingsDialog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUiState,
    eventFlow: Flow<MainEvent>,
    onScreenEvent: (MainUiEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val settingsVm: SettingsViewModel = koinViewModel()

    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is MainEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SetListSync") },
                actions = {
                    Box {
                        IconButton(onClick = {
                            onScreenEvent(MainUiEvent.OnSettingsClicked)
                        }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            // For now empty screen
            Text(
                uiState.contentText,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    if(uiState.showSettingsDialog) {
        val state by settingsVm.uiState.collectAsState()
        SettingsDialog(
            uiState = state,
            eventsFlow = settingsVm.events,
            onScreenEvent = { settingsVm.onScreenEvent(it) },
            onDismiss = { onScreenEvent(MainUiEvent.OnSettingsDismiss) },
            onNavigateToLogin = {
                onScreenEvent(MainUiEvent.OnSettingsDismiss)
                onNavigateToLogin()
            }
        )
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen(MainUiState(), emptyFlow(), {}, {})
}