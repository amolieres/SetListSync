package com.amolieres.setlistync.app.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Full-size centered loading indicator.
 * Pass the Scaffold inner padding as [modifier], e.g. `Modifier.padding(padding)`.
 */
@Composable
fun AppCenteredLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Full-size centered text message (empty state, not-found, etc.).
 * Pass the Scaffold inner padding as [modifier], e.g. `Modifier.padding(padding)`.
 */
@Composable
fun AppCenteredMessage(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text)
    }
}
