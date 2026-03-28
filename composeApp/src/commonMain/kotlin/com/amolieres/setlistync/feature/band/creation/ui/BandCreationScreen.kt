package com.amolieres.setlistync.feature.band.creation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationEvent
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationUiEvent
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationUiState
import com.amolieres.setlistync.feature.band.members.ui.MemberDialog
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandCreationScreen(
    uiState: BandCreationUiState,
    eventsFlow: Flow<BandCreationEvent>,
    onScreenEvent: (BandCreationUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToBandDetail: (String) -> Unit
) {
    LaunchedEffect(eventsFlow) {
        eventsFlow.collect { event ->
            when (event) {
                BandCreationEvent.NavigateBack -> onNavigateBack()
                is BandCreationEvent.NavigateToBandDetail -> onNavigateToBandDetail(event.bandId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nouveau groupe") },
                navigationIcon = {
                    IconButton(onClick = { onScreenEvent(BandCreationUiEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LinearProgressIndicator(
                progress = { uiState.currentStep / 3f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (uiState.currentStep) {
                    1 -> Step1Content(uiState, onScreenEvent)
                    2 -> Step2Content(uiState, onScreenEvent)
                    3 -> Step3Content(uiState, onScreenEvent)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (uiState.currentStep > 1) {
                    OutlinedButton(
                        onClick = { onScreenEvent(BandCreationUiEvent.OnBackClicked) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retour")
                    }
                }
                Button(
                    onClick = { onScreenEvent(BandCreationUiEvent.OnNextClicked) },
                    enabled = uiState.currentStep != 1 || uiState.bandName.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(if (uiState.currentStep == 3) "Créer" else "Suivant")
                    }
                }
            }
        }
    }

    if (uiState.showMemberDialog) {
        MemberDialog(
            title = "Ajouter un membre",
            nickname = uiState.memberNickname,
            selectedRoles = uiState.memberRoles,
            onNicknameChanged = { onScreenEvent(BandCreationUiEvent.OnMemberNicknameChanged(it)) },
            onRoleToggled = { onScreenEvent(BandCreationUiEvent.OnMemberRoleToggled(it)) },
            onConfirm = { onScreenEvent(BandCreationUiEvent.OnMemberDialogConfirm) },
            onDismiss = { onScreenEvent(BandCreationUiEvent.OnMemberDialogDismiss) }
        )
    }
}

@Composable
private fun Step1Content(
    uiState: BandCreationUiState,
    onScreenEvent: (BandCreationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Étape 1 — Informations principales",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = uiState.bandName,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnBandNameChanged(it)) },
            label = { Text("Nom du groupe *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.genreInput,
                onValueChange = { onScreenEvent(BandCreationUiEvent.OnGenreInputChanged(it)) },
                label = { Text("Ajouter un style") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    onScreenEvent(BandCreationUiEvent.OnAddGenreClicked)
                })
            )
            IconButton(onClick = { onScreenEvent(BandCreationUiEvent.OnAddGenreClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter genre")
            }
        }
        if (uiState.genres.isNotEmpty()) {
            Text("Styles musicaux :", style = MaterialTheme.typography.labelLarge)
            uiState.genres.forEach { genre ->
                GenreChip(genre = genre, onRemove = { onScreenEvent(BandCreationUiEvent.OnRemoveGenre(genre)) })
            }
        }
    }
}

@Composable
private fun GenreChip(genre: String, onRemove: () -> Unit) {
    InputChip(
        selected = false,
        onClick = onRemove,
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

@Composable
private fun Step2Content(
    uiState: BandCreationUiState,
    onScreenEvent: (BandCreationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Étape 2 — Contact & réseaux sociaux",
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnEmailChanged(it)) },
            label = { Text("Email (optionnel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = uiState.instagramUrl,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnInstagramUrlChanged(it)) },
            label = { Text("Instagram URL (optionnel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.facebookUrl,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnFacebookUrlChanged(it)) },
            label = { Text("Facebook URL (optionnel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.tiktokUrl,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnTiktokUrlChanged(it)) },
            label = { Text("TikTok URL (optionnel)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
private fun Step3Content(
    uiState: BandCreationUiState,
    onScreenEvent: (BandCreationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Étape 3 — Membres",
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = { onScreenEvent(BandCreationUiEvent.OnAddMemberClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ajouter un membre")
        }
        if (uiState.members.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(uiState.members) { member ->
                    ListItem(
                        headlineContent = {
                            Text(member.nickname ?: "Membre sans surnom")
                        },
                        supportingContent = {
                            if (member.roles.isNotEmpty()) {
                                Text(member.roles.joinToString(", ") { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } })
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        } else {
            Text(
                text = "Aucun membre ajouté pour l'instant.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
