package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.amolieres.setlistync.app.designsystem.AppDimens
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * A row combining an [OutlinedTextField] and an [IconButton] (Add) for entering genre tags.
 * Pressing the IME Done action or the Add button both trigger [onAdd].
 */
@Composable
fun AppGenreInput(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    label: String,
    addContentDescription: String = "",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.weight(1f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onAdd() })
        )
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = addContentDescription)
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun AppGenreInputEmptyPreview() {
    AppGenreInput(value = "", onValueChange = {}, onAdd = {}, label = "Ajouter un style")
}

@Preview
@Composable
private fun AppGenreInputFilledPreview() {
    AppGenreInput(value = "Rock", onValueChange = {}, onAdd = {}, label = "Ajouter un style")
}
