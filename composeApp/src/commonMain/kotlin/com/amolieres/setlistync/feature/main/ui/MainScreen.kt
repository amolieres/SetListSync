package com.amolieres.setlistync.feature.main.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.feature.main.presentation.MainEvent
import com.amolieres.setlistync.feature.main.presentation.MainUiEvent
import com.amolieres.setlistync.feature.main.presentation.MainUiState
import com.amolieres.setlistync.feature.settings.presentation.SettingsViewModel
import com.amolieres.setlistync.feature.settings.ui.SettingsDialog
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: MainUiState,
    eventFlow: Flow<MainEvent>,
    onScreenEvent: (MainUiEvent) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToBandDetail: (String) -> Unit = {},
    onNavigateToBandCreation: () -> Unit = {}
) {
    val settingsVm: SettingsViewModel = koinViewModel()

    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is MainEvent.NavigateToLogin -> onNavigateToLogin()
                is MainEvent.NavigateToBandDetail -> onNavigateToBandDetail(event.bandId)
                MainEvent.NavigateToBandCreation -> onNavigateToBandCreation()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    IconButton(onClick = { onScreenEvent(MainUiEvent.OnSettingsClicked) }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(Res.string.main_cd_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onScreenEvent(MainUiEvent.OnCreateBandClicked) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.main_cd_create_band))
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> AppCenteredLoader(Modifier.padding(padding))

            uiState.bands.isEmpty() -> AppCenteredMessage(
                text = stringResource(Res.string.main_empty),
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(
                    start = AppDimens.SpacingL,
                    end = AppDimens.SpacingL,
                    top = AppDimens.SpacingM,
                    bottom = AppDimens.FabSpacing
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
            ) {
                items(uiState.bands) { band ->
                    BandItem(
                        band = band,
                        onClick = { onScreenEvent(MainUiEvent.OnBandClicked(band.id)) }
                    )
                }
            }
        }
    }

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
