package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun SettingsDialog(
    uiState: SettingsUiState,
    eventsFlow: Flow<SettingsEvent>,
    onScreenEvent: (SettingsUiEvent) -> Unit,
    onDismiss: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(eventsFlow) {
        eventsFlow.collect { event ->
            when (event) {
                SettingsEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.settings_title)) },
        text = {
            HorizontalDivider()
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text(stringResource(Res.string.settings_section_account), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(AppDimens.SpacingS))
                Text("${uiState.userName} (${uiState.userEmail})")
                Spacer(Modifier.height(AppDimens.SpacingM))

                Button(onClick = { onScreenEvent(SettingsUiEvent.OnLogoutClicked) }) {
                    Text(stringResource(Res.string.settings_btn_logout))
                }
                Spacer(Modifier.height(AppDimens.SpacingS))
                Button(
                    onClick = { onScreenEvent(SettingsUiEvent.OnDeleteAccountClicked) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(Res.string.settings_btn_delete_account))
                }

                Spacer(Modifier.height(AppDimens.SpacingL))

                Text(stringResource(Res.string.settings_section_display), style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(AppDimens.SpacingS))
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
        },
        confirmButton = {
            HorizontalDivider()
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_close)) }
        }
    )

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

@Preview
@Composable
fun SettingsDialogPreview() {
    SettingsDialog(
        SettingsUiState(isLoading = true, userEmail = "mail@mail.com", userName = "userName"),
        emptyFlow(), {}, {}, {}
    )
}
