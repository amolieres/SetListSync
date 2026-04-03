package com.amolieres.setlistync.feature.band.songs.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongKey
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    horizontal = AppDimens.SpacingL,
                    vertical = AppDimens.SpacingM
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
            ) {
                items(uiState.songs) { song ->
                    SongItem(
                        song = song,
                        noteNotation = uiState.noteNotation,
                        onEdit = { onScreenEvent(BandSongsUiEvent.OnEditSongClicked(song.id)) },
                        onDelete = { onScreenEvent(BandSongsUiEvent.OnDeleteSongClicked(song.id)) }
                    )
                }
                // Extra bottom padding to clear the FAB
                item { Spacer(Modifier.height(AppDimens.FabSpacing)) }
            }
        }
    }
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
                Song(id = SongId("1"), title = "Highway to Hell", durationSeconds = 208, originalArtist = "AC/DC", key = SongKey.A_MINOR, tempo = 116),
                Song(id = SongId("2"), title = "Summer Rain", durationSeconds = 213, originalArtist = "The Rocketeers", key = SongKey.A_MINOR, tempo = 120),
                Song(id = SongId("3"), title = "Electric Nights", durationSeconds = 187)
            )
        ),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToNewSong = {},
        onNavigateToEditSong = {}
    )
}
