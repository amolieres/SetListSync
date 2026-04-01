package com.amolieres.setlistync.feature.band.songs.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsEvent
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsUiEvent
import com.amolieres.setlistync.feature.band.songs.presentation.BandSongsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import setlistsync.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandSongsScreen(
    uiState: BandSongsUiState,
    eventFlow: Flow<BandSongsEvent>,
    onScreenEvent: (BandSongsUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToNewSong: () -> Unit,
    onNavigateToEditSong: (SongId) -> Unit
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandSongsEvent.NavigateBack -> onNavigateBack()
                BandSongsEvent.NavigateToNewSong -> onNavigateToNewSong()
                is BandSongsEvent.NavigateToEditSong -> onNavigateToEditSong(event.songId)
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
                        onEdit = { onScreenEvent(BandSongsUiEvent.OnEditSongClicked(song.id)) },
                        onDelete = { onScreenEvent(BandSongsUiEvent.OnDeleteSongClicked(song.id)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

// ── Song list item ────────────────────────────────────────────────────────────

@Composable
private fun SongListItem(song: Song, onEdit: () -> Unit, onDelete: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onEdit),
        headlineContent = { Text(song.title) },
        supportingContent = {
            Column {
                song.originalArtist?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
                val parts = buildList {
                    add(song.durationSeconds.formatDuration())
                    song.key?.let { add(it) }
                    song.tempo?.let { add("$it BPM") }
                }
                Text(
                    parts.joinToString("  ·  "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
        onNavigateBack = {},
        onNavigateToNewSong = {},
        onNavigateToEditSong = {}
    )
}

@Preview
@Composable
fun BandSongsScreenEmptyPreview() {
    BandSongsScreen(
        uiState = BandSongsUiState(isLoading = false, bandName = "The Rocketeers"),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToNewSong = {},
        onNavigateToEditSong = {}
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
        onNavigateBack = {},
        onNavigateToNewSong = {},
        onNavigateToEditSong = {}
    )
}
