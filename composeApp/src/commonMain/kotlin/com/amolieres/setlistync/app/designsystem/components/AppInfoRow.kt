package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens

/**
 * A row displaying a leading [icon] alongside a [text] value.
 * An optional [label] is rendered above the value in a smaller style.
 */
@Composable
fun AppInfoRow(
    icon: ImageVector,
    text: String,
    label: String? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(AppDimens.IconSizeMedium),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column {
            if (label != null) {
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun AppInfoRowSimplePreview() {
    AppInfoRow(icon = Icons.Default.Email, text = "contact@band.com")
}

@Preview
@Composable
private fun AppInfoRowWithLabelPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingXs)) {
        AppInfoRow(icon = Icons.Default.Share, text = "instagram.com/myband", label = "Instagram")
        AppInfoRow(icon = Icons.Default.Share, text = "facebook.com/myband", label = "Facebook")
    }
}
