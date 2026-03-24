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
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.core.data.preferences.NoteNotation
import com.amolieres.setlistync.feature.settings.presentation.ConfirmDialogType
import com.amolieres.setlistync.feature.settings.presentation.SettingsEvent
import com.amolieres.setlistync.feature.settings.presentation.SettingsUiEvent
import com.amolieres.setlistync.feature.settings.presentation.SettingsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
        title = { Text("Settings") },
        text = {
            HorizontalDivider()
            Column(Modifier.verticalScroll(rememberScrollState())) {
                // --- My Account ---
                Text("Account", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("${uiState.userName} (${uiState.userEmail})")
                Spacer(Modifier.height(12.dp))

                Button(onClick = { onScreenEvent(SettingsUiEvent.OnLogoutClicked) }) {
                    Text("Log out")
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { onScreenEvent(SettingsUiEvent.OnDeleteAccountClicked) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }

                Spacer(Modifier.height(16.dp))

                // --- Preferences ---
                Text("Display", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                // Notation radio buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.notation == NoteNotation.FR,
                        onClick = { onScreenEvent(SettingsUiEvent.NotationChanged(NoteNotation.FR)) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("French (Do Ré Mi...)")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = uiState.notation == NoteNotation.EN,
                        onClick = { onScreenEvent(SettingsUiEvent.NotationChanged(NoteNotation.EN)) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("American (C D E...)")
                }
            }

        },
        confirmButton = {
            HorizontalDivider()
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )

    // Dialogs
    uiState.showConfirmDialog?.let { dialogType ->
        AlertDialog(
            onDismissRequest = { onScreenEvent(SettingsUiEvent.OnDialogDismiss) },
            title = { Text(when(dialogType) {
                ConfirmDialogType.Logout -> "Logout"
                ConfirmDialogType.DeleteAccount -> "Delete account"
            }) },
            text = { Text(when(dialogType) {
                ConfirmDialogType.Logout -> "Are you sure you want to log out?"
                ConfirmDialogType.DeleteAccount -> "This will permanently delete your account. Continue?"
            }) },
            confirmButton = {
                TextButton(onClick = {
                    onScreenEvent(when(dialogType) {
                        ConfirmDialogType.Logout -> SettingsUiEvent.OnLogoutConfirmed
                        ConfirmDialogType.DeleteAccount -> SettingsUiEvent.OnDeleteConfirmed
                    })
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(SettingsUiEvent.OnDialogDismiss)  }) { Text("Cancel") }
            }
        )
    }
}



@Preview
@Composable
fun SettingsDialogPreview() {
    SettingsDialog(
        SettingsUiState(isLoading = true, userEmail = "mail@mail.com", userName = "userName"), emptyFlow(), {}, {}, {}
    )
}
