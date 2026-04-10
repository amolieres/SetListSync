package com.amolieres.setlistync.feature.band.gigDetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.feature.band.gigDetail.presentation.GigDetailEvent
import com.amolieres.setlistync.feature.band.gigDetail.presentation.GigDetailUiEvent
import com.amolieres.setlistync.feature.band.gigDetail.presentation.GigDetailUiState
import com.amolieres.setlistync.feature.band.songs.ui.SongItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.action_back
import setlistsync.composeapp.generated.resources.gig_action_edit
import setlistsync.composeapp.generated.resources.gig_add_songs
import setlistsync.composeapp.generated.resources.gig_detail_title_create
import setlistsync.composeapp.generated.resources.gig_detail_title_edit
import setlistsync.composeapp.generated.resources.gig_field_date_none
import setlistsync.composeapp.generated.resources.gig_field_expected_duration
import setlistsync.composeapp.generated.resources.gig_field_venue
import setlistsync.composeapp.generated.resources.gig_setlist_empty
import setlistsync.composeapp.generated.resources.gig_setlist_section
import kotlin.time.ExperimentalTime
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun GigDetailScreen(
    uiState: GigDetailUiState,
    eventFlow: Flow<GigDetailEvent>,
    onScreenEvent: (GigDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    isEditMode: Boolean = false
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                GigDetailEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    // Local list for optimistic drag-to-reorder visual feedback.
    // Synced from ViewModel state when not dragging.
    val songs = remember { mutableStateListOf<Song>() }
    LaunchedEffect(uiState.setlistSongs) {
        songs.clear()
        songs.addAll(uiState.setlistSongs)
    }

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // from.index / to.index are global LazyColumn indices (they include the header
        // items before the songs). Use the item key — which is song.id.value (String) —
        // to locate the correct position inside the local songs list.
        val fromIndex = songs.indexOfFirst { it.id.value == from.key }
        val toIndex = songs.indexOfFirst { it.id.value == to.key }
        if (fromIndex != -1 && toIndex != -1) {
            songs.apply { add(toIndex, removeAt(fromIndex)) }
        }
    }

    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.dateMillis
        )
        DatePickerDialog(
            onDismissRequest = { onScreenEvent(GigDetailUiEvent.OnDatePickerDismissed) },
            confirmButton = {
                TextButton(onClick = {
                    onScreenEvent(GigDetailUiEvent.OnDateSelected(datePickerState.selectedDateMillis))
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(GigDetailUiEvent.OnDatePickerDismissed) }) {
                    Text(stringResource(Res.string.action_back))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
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
                    Text(
                        stringResource(
                            if (isEditMode) Res.string.gig_detail_title_edit
                            else Res.string.gig_detail_title_create
                        )
                    )
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
                    if (isEditMode) {
                        IconButton(onClick = { onScreenEvent(GigDetailUiEvent.OnToggleEditing) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(Res.string.gig_action_edit),
                                tint = if (uiState.isEditing) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
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
            // ── Venue ──────────────────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = uiState.venueInput,
                    onValueChange = { onScreenEvent(GigDetailUiEvent.OnVenueChanged(it)) },
                    label = { Text(stringResource(Res.string.gig_field_venue)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = uiState.isEditing
                )
            }

            // ── Date ───────────────────────────────────────────────────────
            item {
                OutlinedButton(
                    onClick = { onScreenEvent(GigDetailUiEvent.OnDatePickerOpen) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.isEditing
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimens.IconSizeSmall)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(
                        uiState.dateMillis?.let { ms ->
                            kotlin.time.Instant.fromEpochMilliseconds(ms).toString().take(10)
                        } ?: stringResource(Res.string.gig_field_date_none)
                    )
                }
            }

            // ── Expected duration ──────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = uiState.expectedDurationInput,
                    onValueChange = { onScreenEvent(GigDetailUiEvent.OnExpectedDurationChanged(it)) },
                    label = { Text(stringResource(Res.string.gig_field_expected_duration)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = uiState.isEditing
                )
            }

            // ── Setlist header ─────────────────────────────────────────────
            item {
                Spacer(Modifier.height(AppDimens.SpacingS))
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
                        // draggableHandle() is an extension on ReorderableItemScope,
                        // so it must be built here and forwarded as a plain Modifier.
                        val handleModifier = Modifier.draggableHandle(
                            onDragStopped = {
                                onScreenEvent(
                                    GigDetailUiEvent.OnSetlistReordered(songs.map { it.id })
                                )
                            }
                        )
                        SongItem(
                            song = song,
                            noteNotation = uiState.noteNotation,
                            position = index + 1,
                            onEdit = {},
                            onDelete = if (uiState.isEditing) {
                                { onScreenEvent(GigDetailUiEvent.OnSongRemovedFromSetlist(song.id)) }
                            } else null,
                            dragHandleModifier = if (uiState.isEditing) handleModifier else null
                        )
                    }
                }
            }

            // ── Saving indicator ───────────────────────────────────────────
            if (uiState.isSaving) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(AppDimens.IconSizeMedium)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun GigDetailScreenCreatePreview() {
    GigDetailScreen(
        uiState = GigDetailUiState(),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        isEditMode = false
    )
}
