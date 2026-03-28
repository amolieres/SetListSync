package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.amolieres.setlistync.feature.settings.presentation.ConfirmDialogType
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsConfirmDialog(
    dialogType: ConfirmDialogType,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                when (dialogType) {
                    ConfirmDialogType.Logout -> stringResource(Res.string.settings_confirm_logout_title)
                    ConfirmDialogType.DeleteAccount -> stringResource(Res.string.settings_confirm_delete_title)
                }
            )
        },
        text = {
            Text(
                when (dialogType) {
                    ConfirmDialogType.Logout -> stringResource(Res.string.settings_confirm_logout_message)
                    ConfirmDialogType.DeleteAccount -> stringResource(Res.string.settings_confirm_delete_message)
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(Res.string.action_yes)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.action_cancel)) }
        }
    )
}
