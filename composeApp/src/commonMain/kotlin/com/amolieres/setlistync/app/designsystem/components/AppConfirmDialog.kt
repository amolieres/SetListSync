package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A generic confirmation [AlertDialog] with confirm and dismiss actions.
 *
 * @param destructiveConfirm When `true`, the confirm button label is rendered in
 *   [MaterialTheme.colorScheme.error] to signal a destructive action.
 */
@Composable
fun AppConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructiveConfirm: Boolean = false
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    confirmLabel,
                    color = if (destructiveConfirm) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }
        }
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun AppConfirmDialogPreview() {
    AppConfirmDialog(
        title = "Déconnexion",
        message = "Voulez-vous vraiment vous déconnecter ?",
        confirmLabel = "Oui",
        dismissLabel = "Annuler",
        onConfirm = {},
        onDismiss = {}
    )
}

@Preview
@Composable
private fun AppConfirmDialogDestructivePreview() {
    AppConfirmDialog(
        title = "Supprimer le groupe",
        message = "Cette action est irréversible. Supprimer « The Rocketeers » ?",
        confirmLabel = "Supprimer",
        dismissLabel = "Annuler",
        onConfirm = {},
        onDismiss = {},
        destructiveConfirm = true
    )
}
