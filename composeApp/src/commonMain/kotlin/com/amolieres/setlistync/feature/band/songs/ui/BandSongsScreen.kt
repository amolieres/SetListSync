package com.amolieres.setlistync.feature.band.songs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.app.designsystem.components.AppLoadingButton
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsEvent
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsUiEvent
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsUiState
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandSongsScreen(
    uiState: BandSongsUiState,
    eventFlow: Flow<BandSongsEvent>,
    onScreenEvent: (BandSongsUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandSongsEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.bandName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onScreenEvent(BandSongsUiEvent.OnAddSongClicked) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.band_detail_cd_add_song))
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> AppCenteredLoader(Modifier.padding(padding))

            uiState.songs.isEmpty() -> AppCenteredMessage(
                text = stringResource(Res.string.band_detail_songs_empty),
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = AppDimens.FabSpacing)
            ) {
                items(uiState.songs) { song ->
                    SongListItem(
                        song = song,
                        onDelete = { onScreenEvent(BandSongsUiEvent.OnDeleteSongClicked(song.id)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (uiState.showSongDialog) {
        SongDialog(
            title = uiState.songTitle,
            minutes = uiState.songMinutes,
            seconds = uiState.songSeconds,
            songKey = uiState.songKey,
            tempo = uiState.songTempo,
            isLoading = uiState.isSavingSong,
            onTitleChanged = { onScreenEvent(BandSongsUiEvent.OnSongTitleChanged(it)) },
            onMinutesChanged = { onScreenEvent(BandSongsUiEvent.OnSongMinutesChanged(it)) },
            onSecondsChanged = { onScreenEvent(BandSongsUiEvent.OnSongSecondsChanged(it)) },
            onKeyChanged = { onScreenEvent(BandSongsUiEvent.OnSongKeyChanged(it)) },
            onTempoChanged = { onScreenEvent(BandSongsUiEvent.OnSongTempoChanged(it)) },
            onConfirm = { onScreenEvent(BandSongsUiEvent.OnSongDialogConfirmed) },
            onDismiss = { onScreenEvent(BandSongsUiEvent.OnSongDialogDismiss) }
        )
    }
}

// ── Song list item ────────────────────────────────────────────────────────────

@Composable
private fun SongListItem(song: Song, onDelete: () -> Unit) {
    ListItem(
        headlineContent = { Text(song.title) },
        supportingContent = {
            val parts = buildList {
                add(song.durationSeconds.formatDuration())
                song.key?.let { add(it) }
                song.tempo?.let { add("$it BPM") }
            }
            Text(parts.joinToString("  ·  "))
        },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.song_cd_delete),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

// ── Song dialog ───────────────────────────────────────────────────────────────

@Composable
private fun SongDialog(
    title: String,
    minutes: String,
    seconds: String,
    songKey: String,
    tempo: String,
    isLoading: Boolean,
    onTitleChanged: (String) -> Unit,
    onMinutesChanged: (String) -> Unit,
    onSecondsChanged: (String) -> Unit,
    onKeyChanged: (String) -> Unit,
    onTempoChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.song_dialog_add_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChanged,
                    label = { Text(stringResource(Res.string.song_dialog_label_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minutes,
                        onValueChange = onMinutesChanged,
                        label = { Text(stringResource(Res.string.song_dialog_label_minutes)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = seconds,
                        onValueChange = onSecondsChanged,
                        label = { Text(stringResource(Res.string.song_dialog_label_seconds)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = songKey,
                    onValueChange = onKeyChanged,
                    label = { Text(stringResource(Res.string.song_dialog_label_key)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = tempo,
                    onValueChange = onTempoChanged,
                    label = { Text(stringResource(Res.string.song_dialog_label_tempo)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            AppLoadingButton(
                onClick = onConfirm,
                isLoading = isLoading,
                enabled = title.isNotBlank()
            ) {
                Text(stringResource(Res.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.action_cancel))
            }
        }
    )
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun Int.formatDuration(): String {
    val m = this / 60
    val s = this % 60
    return "$m:${s.toString().padStart(2, '0')}"
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
fun BandSongsScreenLoadingPreview() {
    BandSongsScreen(
        uiState = BandSongsUiState(isLoading = true, bandName = "The Rocketeers"),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun BandSongsScreenEmptyPreview() {
    BandSongsScreen(
        uiState = BandSongsUiState(isLoading = false, bandName = "The Rocketeers"),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun BandSongsScreenContentPreview() {
    BandSongsScreen(
        uiState = BandSongsUiState(
            isLoading = false,
            bandName = "The Rocketeers",
            songs = listOf(
                Song(id = SongId("1"), title = "Summer Rain", durationSeconds = 213, key = "Am", tempo = 120),
                Song(id = SongId("2"), title = "Electric Nights", durationSeconds = 187, key = "E")
            )
        ),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}
