package com.amolieres.setlistync.feature.main.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.feature.main.presentation.MainEvent
import com.amolieres.setlistync.feature.main.presentation.MainUiEvent
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
    onNavigateToLogin: () -> Unit,
    onNavigateToBandDetail: (String) -> Unit = {}
) {
    val settingsVm: SettingsViewModel = koinViewModel()

    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is MainEvent.NavigateToLogin -> onNavigateToLogin()
                is MainEvent.NavigateToBandDetail -> onNavigateToBandDetail(event.bandId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SetListSync") },
                actions = {
                    IconButton(onClick = { onScreenEvent(MainUiEvent.OnSettingsClicked) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onScreenEvent(MainUiEvent.OnCreateBandClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Create band")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.bands.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No bands yet. Tap + to create one.") }

            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(uiState.bands) { band ->
                    ListItem(
                        headlineContent = { Text(band.name) },
                        supportingContent = {
                            Text("${band.members.size} member${if (band.members.size != 1) "s" else ""}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onScreenEvent(MainUiEvent.OnBandClicked(band.id)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    // Create band dialog
    if (uiState.showCreateBandDialog) {
        AlertDialog(
            onDismissRequest = { onScreenEvent(MainUiEvent.OnCreateBandDismiss) },
            title = { Text("New band") },
            text = {
                OutlinedTextField(
                    value = uiState.createBandName,
                    onValueChange = { onScreenEvent(MainUiEvent.OnCreateBandNameChanged(it)) },
                    label = { Text("Band name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { onScreenEvent(MainUiEvent.OnCreateBandConfirmed) },
                    enabled = uiState.createBandName.isNotBlank()
                ) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(MainUiEvent.OnCreateBandDismiss) }) { Text("Cancel") }
            }
        )
    }

    // Settings dialog
    if (uiState.showSettingsDialog) {
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
    MainScreen(MainUiState(isLoading = false), emptyFlow(), {}, {})
}
