package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.action_delete
import setlistsync.composeapp.generated.resources.action_edit

/**
 * A two-button row displayed when a screen is in edit mode.
 * Left button triggers an edit action; right button triggers a destructive delete action.
 */
@Composable
fun AppEditModeActionRow(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
    ) {
        OutlinedButton(
            onClick = onEditClick,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.action_edit))
        }
        OutlinedButton(
            onClick = onDeleteClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(stringResource(Res.string.action_delete))
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
fun AppEditModeActionRowPreview() {
    AppEditModeActionRow(onEditClick = {}, onDeleteClick = {})
}
