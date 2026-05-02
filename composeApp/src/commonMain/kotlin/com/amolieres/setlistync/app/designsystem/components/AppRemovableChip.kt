package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens

/**
 * An [InputChip] with a trailing close icon.
 * Tapping anywhere on the chip (or the icon) triggers [onRemove].
 */
@Composable
fun AppRemovableChip(
    label: String,
    onRemove: () -> Unit,
    removeContentDescription: String = ""
) {
    InputChip(
        selected = false,
        onClick = onRemove,
        label = { Text(label) },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = removeContentDescription,
                modifier = Modifier.size(AppDimens.IconSizeSmall)
            )
        }
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
fun AppRemovableChipPreview() {
    AppRemovableChip(label = "Rock", onRemove = {})
}
