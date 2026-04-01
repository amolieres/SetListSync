package com.amolieres.setlistync.feature.band.songDetail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppLoadingButton
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailEvent
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailUiEvent
import com.amolieres.setlistync.feature.band.songDetail.presentation.BandSongDetailUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnTitleChanged(it)) },
                    label = { Text(stringResource(Res.string.song_dialog_label_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.originalArtist,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnOriginalArtistChanged(it)) },
                    label = { Text(stringResource(Res.string.song_dialog_label_original_artist)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
                OutlinedTextField(
                    value = uiState.key,
                    onValueChange = { onScreenEvent(BandSongDetailUiEvent.OnKeyChanged(it)) },
                    label = { Text(stringResource(Res.string.song_dialog_label_key)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
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
            key = "Am",
            tempo = "120"
        ),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}
