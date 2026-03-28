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
import kotlinx.coroutines.flow.Flow

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
                title = { Text("Modifier le groupe") },
                navigationIcon = {
                    IconButton(onClick = { onScreenEvent(BandEditUiEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
            ) { Text("Groupe introuvable.") }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Nom ──────────────────────────────────────────────────────
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnNameChanged(it)) },
                    label = { Text("Nom du groupe *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                HorizontalDivider()

                // ── Styles musicaux ──────────────────────────────────────────
                Text("Styles musicaux", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = uiState.genreInput,
                        onValueChange = { onScreenEvent(BandEditUiEvent.OnGenreInputChanged(it)) },
                        label = { Text("Ajouter un style") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            onScreenEvent(BandEditUiEvent.OnAddGenreClicked)
                        })
                    )
                    IconButton(onClick = { onScreenEvent(BandEditUiEvent.OnAddGenreClicked) }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter style")
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
                                        contentDescription = "Supprimer $genre",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalDivider()

                // ── Contact ───────────────────────────────────────────────────
                Text("Contact", style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnEmailChanged(it)) },
                    label = { Text("Email (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                HorizontalDivider()

                // ── Réseaux sociaux ──────────────────────────────────────────
                Text("Réseaux sociaux", style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = uiState.instagramUrl,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnInstagramChanged(it)) },
                    label = { Text("Instagram URL (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.facebookUrl,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnFacebookChanged(it)) },
                    label = { Text("Facebook URL (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.tiktokUrl,
                    onValueChange = { onScreenEvent(BandEditUiEvent.OnTiktokChanged(it)) },
                    label = { Text("TikTok URL (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(8.dp))

                // ── Bouton Enregistrer ────────────────────────────────────────
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
                        Text("Enregistrer")
                    }
                }
            }
        }
    }
}
