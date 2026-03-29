package com.amolieres.setlistync.feature.band.edit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.app.designsystem.components.AppGenreInput
import com.amolieres.setlistync.app.designsystem.components.AppLoadingButton
import com.amolieres.setlistync.app.designsystem.components.AppRemovableChip
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditEvent
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditUiEvent
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditUiState
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BandEditScreen(
    uiState: BandEditUiState,
    eventsFlow: Flow<BandEditEvent>,
    onScreenEvent: (BandEditUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(eventsFlow) {
        eventsFlow.collect { event ->
            when (event) {
                BandEditEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.band_edit_title)) },
                navigationIcon = {
                    IconButton(onClick = { onScreenEvent(BandEditUiEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> AppCenteredLoader(Modifier.padding(padding))

            uiState.bandNotFound -> AppCenteredMessage(
                text = stringResource(Res.string.band_edit_not_found),
                modifier = Modifier.padding(padding)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = AppDimens.SpacingL, vertical = AppDimens.SpacingM),
                verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingL)
            ) {
                // ── Name ─────────────────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnNameChanged(it)) },
                    label = { Text(stringResource(Res.string.label_band_name_required)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                HorizontalDivider()

                // ── Musical styles ───────────────────────────────────────────
                Text(stringResource(Res.string.label_musical_styles), style = MaterialTheme.typography.labelLarge)
                AppGenreInput(
                    value = uiState.genreInput,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnGenreInputChanged(it)) },
                    onAdd = { onScreenEvent(BandEditUiEvent.OnAddGenreClicked) },
                    label = stringResource(Res.string.label_add_style),
                    addContentDescription = stringResource(Res.string.cd_add_style)
                )
                if (uiState.genres.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingXxs)) {
                        uiState.genres.forEach { genre ->
                            AppRemovableChip(
                                label = genre,
                                onRemove = { onScreenEvent(BandEditUiEvent.OnRemoveGenre(genre)) },
                                removeContentDescription = stringResource(Res.string.cd_remove_genre, genre)
                            )
                        }
                    }
                }

                HorizontalDivider()

                // ── Contact ───────────────────────────────────────────────────
                Text(stringResource(Res.string.label_contact), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnEmailChanged(it)) },
                    label = { Text(stringResource(Res.string.label_email_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                HorizontalDivider()

                // ── Social networks ──────────────────────────────────────────
                Text(stringResource(Res.string.label_social_networks), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = uiState.instagramUrl,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnInstagramChanged(it)) },
                    label = { Text(stringResource(Res.string.label_instagram_url_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.facebookUrl,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnFacebookChanged(it)) },
                    label = { Text(stringResource(Res.string.label_facebook_url_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.tiktokUrl,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnTiktokChanged(it)) },
                    label = { Text(stringResource(Res.string.label_tiktok_url_optional)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ── Save button ───────────────────────────────────────────────
                AppLoadingButton(
                    onClick = { onScreenEvent(BandEditUiEvent.OnSaveClicked) },
                    isLoading = uiState.isSaving,
                    enabled = uiState.name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.action_save))
                }
            }
        }
    }
}
