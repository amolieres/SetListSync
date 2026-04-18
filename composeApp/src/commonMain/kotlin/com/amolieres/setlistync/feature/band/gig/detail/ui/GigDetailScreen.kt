package com.amolieres.setlistync.feature.band.gig.detail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppEditModeActionRow
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.util.formatSetlistDuration
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailEvent
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailUiEvent
import com.amolieres.setlistync.feature.band.gig.detail.presentation.GigDetailUiState
import com.amolieres.setlistync.feature.band.songs.ui.SongItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.action_back
import setlistsync.composeapp.generated.resources.action_cancel
import setlistsync.composeapp.generated.resources.action_confirm
import setlistsync.composeapp.generated.resources.action_done
import setlistsync.composeapp.generated.resources.gig_action_edit
import setlistsync.composeapp.generated.resources.gig_add_songs
import setlistsync.composeapp.generated.resources.gig_delete_confirm_message
import setlistsync.composeapp.generated.resources.gig_edit_cd_delete
import setlistsync.composeapp.generated.resources.gig_field_date_none
import setlistsync.composeapp.generated.resources.gig_setlist_empty
import setlistsync.composeapp.generated.resources.gig_setlist_section
import setlistsync.composeapp.generated.resources.gig_summary_no_venue
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun GigDetailScreen(
    uiState: GigDetailUiState,
    eventFlow: Flow<GigDetailEvent>,
    onScreenEvent: (GigDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToEditGig: () -> Unit = {}
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                GigDetailEvent.NavigateBack -> onNavigateBack()
                GigDetailEvent.NavigateToEditGig -> onNavigateToEditGig()
            }
        }
    }

    val showDeleteConfirmDialog = remember { mutableStateOf(false) }

    // Local list for optimistic drag-to-reorder visual feedback.
    val songs = remember { mutableStateListOf<Song>() }
    LaunchedEffect(uiState.setlistSongs) {
        songs.clear()
        songs.addAll(uiState.setlistSongs)
    }

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromIndex = songs.indexOfFirst { it.id.value == from.key }
        val toIndex = songs.indexOfFirst { it.id.value == to.key }
        if (fromIndex != -1 && toIndex != -1) {
            songs.apply { add(toIndex, removeAt(fromIndex)) }
        }
    }

    if (showDeleteConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog.value = false },
            title = { Text(stringResource(Res.string.gig_edit_cd_delete)) },
            text = { Text(stringResource(Res.string.gig_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmDialog.value = false
                    onScreenEvent(GigDetailUiEvent.OnDeleteGigClicked)
                }) {
                    Text(stringResource(Res.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog.value = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    if (uiState.showAddSongsSheet) {
        ModalBottomSheet(
            onDismissRequest = { onScreenEvent(GigDetailUiEvent.OnAddSongsDismissed) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Text(
                text = stringResource(Res.string.gig_add_songs),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    horizontal = AppDimens.SpacingL,
                    vertical = AppDimens.SpacingM
                )
            )
            if (uiState.catalogSongs.isEmpty()) {
                Text(
                    text = stringResource(Res.string.gig_setlist_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(AppDimens.SpacingL)
                )
            } else {
                uiState.catalogSongs.forEach { song ->
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = song.originalArtist?.let { { Text(it) } },
                        trailingContent = {
                            IconButton(onClick = {
                                onScreenEvent(GigDetailUiEvent.OnSongAddedToSetlist(song.id))
                            }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
            Spacer(Modifier.height(AppDimens.SpacingL))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.gig?.venue
                                ?: stringResource(Res.string.gig_summary_no_venue),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (uiState.gig?.venue == null)
                                MaterialTheme.colorScheme.onSurfaceVariant
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = uiState.gig?.date?.toString()?.take(10)
                                ?: stringResource(Res.string.gig_field_date_none),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onScreenEvent(GigDetailUiEvent.OnToggleEditing) }) {
                        if (uiState.isEditing) {
                            Icon(
                                Icons.Default.Done,
                                contentDescription = stringResource(Res.string.action_done)
                            )
                        } else {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.gig_action_edit)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            AppCenteredLoader(Modifier.padding(padding))
            return@Scaffold
        }

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(AppDimens.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
        ) {
            // ── Editing actions (replaces summary card when isEditing) ──────
            if (uiState.isEditing) {
                item {
                    AppEditModeActionRow(
                        onEditClick = { onScreenEvent(GigDetailUiEvent.OnEditGigInfoClicked) },
                        onDeleteClick = { showDeleteConfirmDialog.value = true }
                    )
                }
            }

            // ── Setlist header ─────────────────────────────────────────────
            item {
                Spacer(Modifier.height(AppDimens.SpacingS))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            stringResource(Res.string.gig_setlist_section),
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (uiState.setlistDurationSeconds > 0) {
                            Text(
                                text = uiState.setlistDurationSeconds.formatSetlistDuration(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (uiState.isEditing) {
                        IconButton(onClick = { onScreenEvent(GigDetailUiEvent.OnAddSongsClicked) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(Res.string.gig_add_songs)
                            )
                        }
                    }
                }
            }

            // ── Setlist songs ──────────────────────────────────────────────
            if (songs.isEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.gig_setlist_empty),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                itemsIndexed(songs, key = { _, song -> song.id.value }) { index, song ->
                    ReorderableItem(reorderState, key = song.id.value) {
                        val handleModifier = if (uiState.isEditing) {
                            Modifier.draggableHandle(
                                onDragStopped = {
                                    onScreenEvent(
                                        GigDetailUiEvent.OnSetlistReordered(songs.map { it.id })
                                    )
                                }
                            )
                        } else null
                        SongItem(
                            song = song,
                            noteNotation = uiState.noteNotation,
                            position = index + 1,
                            onEdit = {},
                            onDelete = if (uiState.isEditing) {
                                { onScreenEvent(GigDetailUiEvent.OnSongRemovedFromSetlist(song.id)) }
                            } else null,
                            dragHandleModifier = handleModifier
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GigDetailScreenPreview() {
    GigDetailScreen(
        uiState = GigDetailUiState(isLoading = false),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun GigDetailScreenEditingPreview() {
    GigDetailScreen(
        uiState = GigDetailUiState(isLoading = false, isEditing = true),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}
