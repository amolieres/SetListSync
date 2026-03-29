package com.amolieres.setlistync.feature.band.edit.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
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
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.bandNotFound -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(stringResource(Res.string.band_edit_not_found)) }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.genreInput,
                        onValueChange = { onScreenEvent(BandEditUiEvent.OnGenreInputChanged(it)) },
                        label = { Text(stringResource(Res.string.label_add_style)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            onScreenEvent(BandEditUiEvent.OnAddGenreClicked)
                        })
                    )
                    IconButton(onClick = { onScreenEvent(BandEditUiEvent.OnAddGenreClicked) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.cd_add_style))
                    }
                }
                if (uiState.genres.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        uiState.genres.forEach { genre ->
                            InputChip(
                                selected = false,
                                onClick = { onScreenEvent(BandEditUiEvent.OnRemoveGenre(genre)) },
                                label = { Text(genre) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(Res.string.cd_remove_genre, genre),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
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

                Spacer(Modifier.height(8.dp))

                // ── Save button ───────────────────────────────────────────────
                Button(
                    onClick = { onScreenEvent(BandEditUiEvent.OnSaveClicked) },
                    enabled = uiState.name.isNotBlank() && !uiState.isSaving,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(stringResource(Res.string.action_save))
                    }
                }
            }
        }
    }
}
