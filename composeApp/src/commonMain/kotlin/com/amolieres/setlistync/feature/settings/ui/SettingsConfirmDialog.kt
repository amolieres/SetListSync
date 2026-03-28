package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.amolieres.setlistync.feature.settings.presentation.ConfirmDialogType

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
                    ConfirmDialogType.Logout -> "Logout"
                    ConfirmDialogType.DeleteAccount -> "Delete account"
                }
            )
        },
        text = {
            Text(
                when (dialogType) {
                    ConfirmDialogType.Logout -> "Are you sure you want to log out?"
                    ConfirmDialogType.DeleteAccount -> "This will permanently delete your account. Continue?"
                }
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Yes") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
