package com.amolieres.setlistync.feature.band.gigDetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.feature.band.gigDetail.presentation.GigDetailEvent
import com.amolieres.setlistync.feature.band.gigDetail.presentation.GigDetailUiEvent
import com.amolieres.setlistync.feature.band.gigDetail.presentation.GigDetailUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.action_back
import setlistsync.composeapp.generated.resources.action_save
import setlistsync.composeapp.generated.resources.gig_add_songs
import setlistsync.composeapp.generated.resources.gig_detail_title_create
import setlistsync.composeapp.generated.resources.gig_detail_title_edit
import setlistsync.composeapp.generated.resources.gig_field_date_none
import setlistsync.composeapp.generated.resources.gig_field_expected_duration
import setlistsync.composeapp.generated.resources.gig_field_venue
import setlistsync.composeapp.generated.resources.gig_setlist_empty
import setlistsync.composeapp.generated.resources.gig_setlist_section
import setlistsync.composeapp.generated.resources.song_cd_delete
import kotlin.time.ExperimentalTime

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            if (!uiState.isSaving) {
                FloatingActionButton(onClick = { onScreenEvent(GigDetailUiEvent.OnSaveClicked) }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.action_save))
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            AppCenteredLoader(Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppDimens.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
        ) {

            // ── Venue ──────────────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.venueInput,
                onValueChange = { onScreenEvent(GigDetailUiEvent.OnVenueChanged(it)) },
                label = { Text(stringResource(Res.string.gig_field_venue)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Date ───────────────────────────────────────────────────────
            OutlinedButton(
                onClick = { onScreenEvent(GigDetailUiEvent.OnDatePickerOpen) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconSizeSmall)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    uiState.dateMillis?.let { ms ->
                        // ISO-8601 prefix gives YYYY-MM-DD (UTC), e.g. "2025-12-25"
                        kotlin.time.Instant.fromEpochMilliseconds(ms).toString().take(10)
                    } ?: stringResource(Res.string.gig_field_date_none)
                )
            }

            // ── Expected duration ──────────────────────────────────────────
            OutlinedTextField(
                value = uiState.expectedDurationInput,
                onValueChange = { onScreenEvent(GigDetailUiEvent.OnExpectedDurationChanged(it)) },
                label = { Text(stringResource(Res.string.gig_field_expected_duration)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(AppDimens.SpacingS))

            // ── Setlist section ────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(Res.string.gig_setlist_section),
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = { onScreenEvent(GigDetailUiEvent.OnAddSongsClicked) }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.gig_add_songs))
                }
            }

            if (uiState.setlistSongs.isEmpty()) {
                Text(
                    text = stringResource(Res.string.gig_setlist_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                uiState.setlistSongs.forEachIndexed { index, song ->
                    ListItem(
                        overlineContent = { Text("${index + 1}") },
                        headlineContent = { Text(song.title) },
                        supportingContent = song.originalArtist?.let { { Text(it) } },
                        trailingContent = {
                            IconButton(
                                onClick = { onScreenEvent(GigDetailUiEvent.OnSongRemovedFromSetlist(song.id)) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = stringResource(Res.string.song_cd_delete),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    )
                    if (index < uiState.setlistSongs.lastIndex) HorizontalDivider()
                }
            }

            if (uiState.isSaving) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(AppDimens.IconSizeMedium))
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
