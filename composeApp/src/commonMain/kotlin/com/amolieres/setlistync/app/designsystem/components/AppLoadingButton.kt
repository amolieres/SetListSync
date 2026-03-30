package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A [Button] that swaps its content for a [CircularProgressIndicator] while [isLoading] is true.
 * The button is automatically disabled during loading.
 */
@Composable
fun AppLoadingButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(AppDimens.ButtonLoaderSize),
                strokeWidth = AppDimens.ButtonLoaderStrokeWidth,
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            content()
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun AppLoadingButtonIdlePreview() {
    AppLoadingButton(onClick = {}, isLoading = false) {
        Text("Enregistrer")
    }
}

@Preview
@Composable
private fun AppLoadingButtonLoadingPreview() {
    AppLoadingButton(onClick = {}, isLoading = true) {
        Text("Enregistrer")
    }
}
