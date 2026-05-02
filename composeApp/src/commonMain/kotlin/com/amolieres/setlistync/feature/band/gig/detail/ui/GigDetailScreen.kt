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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import setlistsync.composeapp.generated.resources.action_save
import setlistsync.composeapp.generated.resources.gig_action_edit
import setlistsync.composeapp.generated.resources.gig_add_set
import setlistsync.composeapp.generated.resources.gig_add_songs
import setlistsync.composeapp.generated.resources.gig_delete_confirm_message
import setlistsync.composeapp.generated.resources.gig_edit_cd_delete
import setlistsync.composeapp.generated.resources.gig_field_date_none
import setlistsync.composeapp.generated.resources.gig_remove_set
import setlistsync.composeapp.generated.resources.gig_set_default_title
import setlistsync.composeapp.generated.resources.gig_set_title_dialog_hint
import setlistsync.composeapp.generated.resources.gig_setlist_empty
import setlistsync.composeapp.generated.resources.gig_setlist_section
import setlistsync.composeapp.generated.resources.gig_stats_duration
import setlistsync.composeapp.generated.resources.gig_stats_songs
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

    // Flat local list for optimistic drag-to-reorder visual feedback.
    // Each entry is (setId, Song).
    val flatSongs = remember { mutableStateListOf<Pair<String, Song>>() }
    LaunchedEffect(uiState.sets) {
        flatSongs.clear()
        uiState.sets.forEach { set ->
            set.songs.forEach { song -> flatSongs.add(set.setId to song) }
        }
    }

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromKey = from.key as String
        val toKey = to.key as String
        // Only allow reordering within the same set
        val fromSetId = fromKey.substringBefore("::")
        val toSetId = toKey.substringBefore("::")
        if (fromSetId != toSetId) return@rememberReorderableLazyListState
        val fromIndex = flatSongs.indexOfFirst { "${it.first}::${it.second.id.value}" == fromKey }
        val toIndex = flatSongs.indexOfFirst { "${it.first}::${it.second.id.value}" == toKey }
        if (fromIndex != -1 && toIndex != -1) {
            flatSongs.add(toIndex, flatSongs.removeAt(fromIndex))
        }
    }

    // Delete gig confirmation
    if (showDeleteConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog.value = false },
            title = { Text(stringResource(Res.string.gig_edit_cd_delete)) },
            text = { Text(stringResource(Res.string.gig_delete_confirm_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirmDialog.value = false
                    onScreenEvent(GigDetailUiEvent.OnDeleteGigClicked)
                }) { Text(stringResource(Res.string.action_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog.value = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    // Set title edit dialog
    if (uiState.editingSetTitleSetId != null) {
        AlertDialog(
            onDismissRequest = { onScreenEvent(GigDetailUiEvent.OnSetTitleDismissed) },
            title = { Text(stringResource(Res.string.gig_set_title_dialog_hint)) },
            text = {
                OutlinedTextField(
                    value = uiState.editingSetTitleInput,
                    onValueChange = { onScreenEvent(GigDetailUiEvent.OnSetTitleChanged(it)) },
                    label = { Text(stringResource(Res.string.gig_set_title_dialog_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { onScreenEvent(GigDetailUiEvent.OnSetTitleConfirmed) }) {
                    Text(stringResource(Res.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(GigDetailUiEvent.OnSetTitleDismissed) }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    // Add songs bottom sheet
    if (uiState.addingSongsToSetId != null) {
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
                val targetSetId = uiState.addingSongsToSetId
                uiState.catalogSongs.forEach { song ->
                    ListItem(
                        headlineContent = { Text(song.title) },
                        supportingContent = song.originalArtist?.let { { Text(it) } },
                        trailingContent = {
                            IconButton(onClick = {
                                onScreenEvent(
                                    GigDetailUiEvent.OnSongAddedToSetlist(targetSetId, song.id)
                                )
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
                            style = MaterialTheme.typography.headlineSmall,
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
            // ── Stats card ─────────────────────────────────────────────
            item(key = "stats_card") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(AppDimens.SpacingL)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.gig_stats_songs),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${flatSongs.size}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(Res.string.gig_stats_duration),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = uiState.totalDurationSeconds.formatSetlistDuration(),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            // ── Edit mode actions ──────────────────────────────────────
            if (uiState.isEditing) {
                item(key = "edit_actions") {
                    AppEditModeActionRow(
                        onEditClick = { onScreenEvent(GigDetailUiEvent.OnEditGigInfoClicked) },
                        onDeleteClick = { showDeleteConfirmDialog.value = true }
                    )
                }
            }

            // ── Setlist header ─────────────────────────────────────────
            item(key = "setlist_header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(Res.string.gig_setlist_section),
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (uiState.isEditing) {
                        IconButton(onClick = { onScreenEvent(GigDetailUiEvent.OnAddSetClicked) }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = stringResource(Res.string.gig_add_set)
                            )
                        }
                    }
                }
            }

            // ── Sets ───────────────────────────────────────────────────
            uiState.sets.forEachIndexed { setIndex, set ->
                // Set header
                item(key = "set_header_${set.setId}") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = AppDimens.SpacingS),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = set.title
                                    ?: stringResource(Res.string.gig_set_default_title, setIndex + 1),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (set.durationSeconds > 0) {
                                Text(
                                    text = set.durationSeconds.formatSetlistDuration(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        // Edit title Delete and add songs only in edit mode
                        if (uiState.isEditing) {
                            IconButton(
                                onClick = { onScreenEvent(GigDetailUiEvent.OnEditSetTitleClicked(set.setId)) }
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { onScreenEvent(GigDetailUiEvent.OnRemoveSetClicked(set.setId)) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(Res.string.gig_remove_set),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                            IconButton(
                                onClick = { onScreenEvent(GigDetailUiEvent.OnAddSongsClicked(set.setId)) }
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = stringResource(Res.string.gig_add_songs)
                                )
                            }
                        }
                    }
                    HorizontalDivider()
                }

                // Songs in this set
                val songsInThisSet = flatSongs.filter { it.first == set.setId }
                if (songsInThisSet.isEmpty()) {
                    item(key = "empty_${set.setId}") {
                        Text(
                            text = stringResource(Res.string.gig_setlist_empty),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = AppDimens.SpacingXs)
                        )
                    }
                } else {
                    items(
                        items = songsInThisSet,
                        key = { (setId, song) -> "${setId}::${song.id.value}" }
                    ) { (setId, song) ->
                        val itemKey = "${setId}::${song.id.value}"
                        ReorderableItem(reorderState, key = itemKey) {
                            val handleModifier = if (uiState.isEditing) {
                                Modifier.draggableHandle(
                                    onDragStopped = {
                                        val newOrder = flatSongs
                                            .filter { it.first == setId }
                                            .map { it.second.id }
                                        onScreenEvent(
                                            GigDetailUiEvent.OnSetlistReordered(setId, newOrder)
                                        )
                                    }
                                )
                            } else null
                            // Compute 1-based position within this set
                            val positionInSet = songsInThisSet.indexOfFirst {
                                it.second.id.value == song.id.value
                            } + 1
                            SongItem(
                                song = song,
                                noteNotation = uiState.noteNotation,
                                position = positionInSet,
                                onEdit = {},
                                onDelete = if (uiState.isEditing) {
                                    {
                                        onScreenEvent(
                                            GigDetailUiEvent.OnSongRemovedFromSetlist(setId, song.id)
                                        )
                                    }
                                } else null,
                                dragHandleModifier = handleModifier
                            )
                        }
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
