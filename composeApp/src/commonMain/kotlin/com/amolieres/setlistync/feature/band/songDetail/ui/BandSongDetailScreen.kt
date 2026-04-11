package com.amolieres.setlistync.feature.band.songDetail.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppLoadingButton
import com.amolieres.setlistync.core.domain.song.model.SongKey
import com.amolieres.setlistync.core.util.formatDuration
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailEvent
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailUiEvent
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import setlistsync.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandSongDetailScreen(
    uiState: BandSongDetailUiState,
    eventFlow: Flow<BandSongDetailEvent>,
    onScreenEvent: (BandSongDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandSongDetailEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    val title = if (uiState.isEditMode) {
        stringResource(Res.string.song_detail_title_edit)
    } else {
        stringResource(Res.string.song_detail_title_create)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { onScreenEvent(BandSongDetailUiEvent.OnBackClicked) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            AppCenteredLoader(Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppDimens.SpacingL)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
            ) {
                // ── Search bar ────────────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnSearchQueryChanged(it)) },
                    label = { Text(stringResource(Res.string.song_search_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        if (uiState.isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { onScreenEvent(BandSongDetailUiEvent.OnSearchSubmitted) }) {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { onScreenEvent(BandSongDetailUiEvent.OnSearchSubmitted) }
                    )
                )

                // ── Search results dropdown ───────────────────────────────────
                if (uiState.searchResults.isNotEmpty()) {
                    Card(Modifier.fillMaxWidth()) {
                        LazyColumn(Modifier.heightIn(max = 240.dp)) {
                            items(uiState.searchResults) { result ->
                                ListItem(
                                    modifier = Modifier.clickable {
                                        onScreenEvent(BandSongDetailUiEvent.OnSearchResultSelected(result))
                                    },
                                    headlineContent = { Text(result.title) },
                                    supportingContent = {
                                        Text("${result.artist}  ·  ${result.durationSeconds.formatDuration()}")
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }

                // ── BPM/key loading indicator ─────────────────────────────────
                if (uiState.isLoadingFeatures) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
                    ) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text(
                            stringResource(Res.string.song_search_loading_features),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ── Title ─────────────────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnTitleChanged(it)) },
                    label = { Text(stringResource(Res.string.song_dialog_label_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ── Original artist ───────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.originalArtist,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnOriginalArtistChanged(it)) },
                    label = { Text(stringResource(Res.string.song_dialog_label_original_artist)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ── Duration ──────────────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.minutes,
                        onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnMinutesChanged(it)) },
                        label = { Text(stringResource(Res.string.song_dialog_label_minutes)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(":", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = uiState.seconds,
                        onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnSecondsChanged(it)) },
                        label = { Text(stringResource(Res.string.song_dialog_label_seconds)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                // ── Key — dropdown selector ───────────────────────────────────
                var keyMenuExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = keyMenuExpanded,
                    onExpandedChange = { keyMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = uiState.key?.display(uiState.noteNotation) ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(Res.string.song_dialog_label_key)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = keyMenuExpanded)
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = keyMenuExpanded,
                        onDismissRequest = { keyMenuExpanded = false }
                    ) {
                        // "None" option
                        DropdownMenuItem(
                            text = { Text(stringResource(Res.string.song_dialog_key_none)) },
                            onClick = {
                                onScreenEvent(BandSongDetailUiEvent.OnKeyChanged(null))
                                keyMenuExpanded = false
                            }
                        )
                        HorizontalDivider()
                        SongKey.entries.forEach { key ->
                            DropdownMenuItem(
                                text = { Text(key.display(uiState.noteNotation)) },
                                onClick = {
                                    onScreenEvent(BandSongDetailUiEvent.OnKeyChanged(key))
                                    keyMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // ── Tempo ─────────────────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.tempo,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnTempoChanged(it)) },
                    label = { Text(stringResource(Res.string.song_dialog_label_tempo)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(AppDimens.SpacingS))
                AppLoadingButton(
                    onClick = { onScreenEvent(BandSongDetailUiEvent.OnSaveClicked) },
                    isLoading = uiState.isSaving,
                    enabled = uiState.title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.action_save))
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
fun BandSongDetailScreenCreatePreview() {
    BandSongDetailScreen(
        uiState = BandSongDetailUiState(isEditMode = false),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun BandSongDetailScreenEditPreview() {
    BandSongDetailScreen(
        uiState = BandSongDetailUiState(
            isEditMode = true,
            title = "Summer Rain",
            minutes = "3",
            seconds = "33",
            key = SongKey.A_MINOR,
            tempo = "120"
        ),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}
