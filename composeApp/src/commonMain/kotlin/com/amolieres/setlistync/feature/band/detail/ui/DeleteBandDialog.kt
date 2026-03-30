package com.amolieres.setlistync.feature.band.detail.ui

import androidx.compose.runtime.Composable
import com.amolieres.setlistync.app.designsystem.components.AppConfirmDialog
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeleteBandDialog(
    bandName: String?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AppConfirmDialog(
        title = stringResource(Res.string.delete_band_title),
        message = stringResource(Res.string.delete_band_message, bandName ?: ""),
        confirmLabel = stringResource(Res.string.action_delete),
        dismissLabel = stringResource(Res.string.action_cancel),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        destructiveConfirm = true
    )
}
