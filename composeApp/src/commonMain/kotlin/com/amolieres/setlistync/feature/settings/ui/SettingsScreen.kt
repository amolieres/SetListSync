package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.feature.settings.presentation.ConfirmDialogType
import com.amolieres.setlistync.feature.settings.presentation.SettingsEvent
import com.amolieres.setlistync.feature.settings.presentation.SettingsUiEvent
import com.amolieres.setlistync.feature.settings.presentation.SettingsUiState
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    eventsFlow: Flow<SettingsEvent>,
    onScreenEvent: (SettingsUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(eventsFlow) {
        eventsFlow.collect { event ->
            when (event) {
                SettingsEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppDimens.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
        ) {
            // ── Account ────────────────────────────────────────────────────────
            Text(
                stringResource(Res.string.settings_section_account),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                "${uiState.userName} (${uiState.userEmail})",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = { onScreenEvent(SettingsUiEvent.OnLogoutClicked) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.settings_btn_logout))
            }
            Button(
                onClick = { onScreenEvent(SettingsUiEvent.OnDeleteAccountClicked) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(Res.string.settings_btn_delete_account))
            }

            HorizontalDivider(Modifier.padding(vertical = AppDimens.SpacingS))

            // ── Display ────────────────────────────────────────────────────────
            Text(
                stringResource(Res.string.settings_section_display),
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.notation == NoteNotation.FR,
                    onClick = { onScreenEvent(SettingsUiEvent.NotationChanged(NoteNotation.FR)) }
                )
                Spacer(Modifier.width(AppDimens.SpacingS))
                Text(stringResource(Res.string.settings_notation_french))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = uiState.notation == NoteNotation.EN,
                    onClick = { onScreenEvent(SettingsUiEvent.NotationChanged(NoteNotation.EN)) }
                )
                Spacer(Modifier.width(AppDimens.SpacingS))
                Text(stringResource(Res.string.settings_notation_american))
            }
        }
    }

    uiState.showConfirmDialog?.let { dialogType ->
        SettingsConfirmDialog(
            dialogType = dialogType,
            onConfirm = {
                onScreenEvent(
                    when (dialogType) {
                        ConfirmDialogType.Logout -> SettingsUiEvent.OnLogoutConfirmed
                        ConfirmDialogType.DeleteAccount -> SettingsUiEvent.OnDeleteConfirmed
                    }
                )
            },
            onDismiss = { onScreenEvent(SettingsUiEvent.OnDialogDismiss) }
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        uiState = SettingsUiState(isLoading = false, userName = "John Doe", userEmail = "john@example.com", notation = NoteNotation.EN),
        eventsFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToLogin = {}
    )
}

@Preview
@Composable
fun SettingsScreenLoadingPreview() {
    SettingsScreen(
        uiState = SettingsUiState(isLoading = true),
        eventsFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToLogin = {}
    )
}
