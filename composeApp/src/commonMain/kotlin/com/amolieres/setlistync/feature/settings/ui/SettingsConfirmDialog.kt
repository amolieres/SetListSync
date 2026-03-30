package com.amolieres.setlistync.feature.settings.ui

import androidx.compose.runtime.Composable
import com.amolieres.setlistync.app.designsystem.components.AppConfirmDialog
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
    AppConfirmDialog(
        title = stringResource(
            when (dialogType) {
                ConfirmDialogType.Logout -> Res.string.settings_confirm_logout_title
                ConfirmDialogType.DeleteAccount -> Res.string.settings_confirm_delete_title
            }
        ),
        message = stringResource(
            when (dialogType) {
                ConfirmDialogType.Logout -> Res.string.settings_confirm_logout_message
                ConfirmDialogType.DeleteAccount -> Res.string.settings_confirm_delete_message
            }
        ),
        confirmLabel = stringResource(Res.string.action_yes),
        dismissLabel = stringResource(Res.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}
