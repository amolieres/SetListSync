package com.amolieres.setlistync.feature.band.creation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppGenreInput
import com.amolieres.setlistync.app.designsystem.components.AppLoadingButton
import com.amolieres.setlistync.app.designsystem.components.AppRemovableChip
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationEvent
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationUiEvent
import com.amolieres.setlistync.feature.band.creation.presentation.BandCreationUiState
import com.amolieres.setlistync.feature.band.members.ui.MemberDialog
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import kotlinx.coroutines.flow.Flow
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview

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
                title = { Text(stringResource(Res.string.band_creation_title)) },
                navigationIcon = {
                    IconButton(onClick = { onScreenEvent(BandCreationUiEvent.OnBackClicked) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
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

            Spacer(Modifier.height(AppDimens.SpacingL))

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
                    .padding(horizontal = AppDimens.SpacingL, vertical = AppDimens.SpacingM),
                horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
            ) {
                if (uiState.currentStep > 1) {
                    OutlinedButton(
                        onClick = { onScreenEvent(BandCreationUiEvent.OnBackClicked) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(Res.string.action_back))
                    }
                }
                AppLoadingButton(
                    onClick = { onScreenEvent(BandCreationUiEvent.OnNextClicked) },
                    isLoading = uiState.isLoading,
                    enabled = uiState.currentStep != 1 || uiState.bandName.isNotBlank(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (uiState.currentStep == 3) stringResource(Res.string.band_creation_btn_create) else stringResource(Res.string.action_next))
                }
            }
        }
    }

    if (uiState.showMemberDialog) {
        MemberDialog(
            title = stringResource(Res.string.band_creation_add_member),
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
            .padding(horizontal = AppDimens.SpacingL),
        verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingL)
    ) {
        Text(
            text = stringResource(Res.string.band_creation_step1_title),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = uiState.bandName,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnBandNameChanged(it)) },
            label = { Text(stringResource(Res.string.label_band_name_required)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        AppGenreInput(
            value = uiState.genreInput,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnGenreInputChanged(it)) },
            onAdd = { onScreenEvent(BandCreationUiEvent.OnAddGenreClicked) },
            label = stringResource(Res.string.label_add_style),
            addContentDescription = stringResource(Res.string.band_creation_cd_add_genre)
        )
        if (uiState.genres.isNotEmpty()) {
            Text(stringResource(Res.string.band_creation_genres_section), style = MaterialTheme.typography.labelLarge)
            uiState.genres.forEach { genre ->
                AppRemovableChip(
                    label = genre,
                    onRemove = { onScreenEvent(BandCreationUiEvent.OnRemoveGenre(genre)) },
                    removeContentDescription = stringResource(Res.string.cd_remove_genre, genre)
                )
            }
        }
    }
}

@Composable
private fun Step2Content(
    uiState: BandCreationUiState,
    onScreenEvent: (BandCreationUiEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AppDimens.SpacingL),
        verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingL)
    ) {
        Text(
            text = stringResource(Res.string.band_creation_step2_title),
            style = MaterialTheme.typography.titleMedium
        )
        OutlinedTextField(
            value = uiState.email,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnEmailChanged(it)) },
            label = { Text(stringResource(Res.string.label_email_optional)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        OutlinedTextField(
            value = uiState.instagramUrl,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnInstagramUrlChanged(it)) },
            label = { Text(stringResource(Res.string.label_instagram_url_optional)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.facebookUrl,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnFacebookUrlChanged(it)) },
            label = { Text(stringResource(Res.string.label_facebook_url_optional)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = uiState.tiktokUrl,
            onValueChange = { onScreenEvent(BandCreationUiEvent.OnTiktokUrlChanged(it)) },
            label = { Text(stringResource(Res.string.label_tiktok_url_optional)) },
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
            .padding(horizontal = AppDimens.SpacingL),
        verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingL)
    ) {
        Text(
            text = stringResource(Res.string.band_creation_step3_title),
            style = MaterialTheme.typography.titleMedium
        )
        Button(
            onClick = { onScreenEvent(BandCreationUiEvent.OnAddMemberClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(AppDimens.SpacingS))
            Text(stringResource(Res.string.band_creation_add_member))
        }
        if (uiState.members.isNotEmpty()) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingXxs)) {
                items(uiState.members) { member ->
                    ListItem(
                        headlineContent = {
                            Text(member.nickname ?: stringResource(Res.string.band_creation_member_no_nickname))
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
                text = stringResource(Res.string.band_creation_no_members),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
fun BandCreationScreenStep1Preview() {
    BandCreationScreen(
        uiState = BandCreationUiState(
            currentStep = 1,
            bandName = "The Rocketeers",
            genres = listOf("Rock", "Indie")
        ),
        eventsFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToBandDetail = {}
    )
}

@Preview
@Composable
fun BandCreationScreenStep2Preview() {
    BandCreationScreen(
        uiState = BandCreationUiState(
            currentStep = 2,
            bandName = "The Rocketeers",
            email = "contact@rocketeers.com",
            instagramUrl = "instagram.com/rocketeers"
        ),
        eventsFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToBandDetail = {}
    )
}

@Preview
@Composable
fun BandCreationScreenStep3Preview() {
    BandCreationScreen(
        uiState = BandCreationUiState(
            currentStep = 3,
            bandName = "The Rocketeers",
            members = listOf(
                BandMember(id = "1", userId = null, nickname = "John", roles = listOf(Role.VOCALS, Role.GUITAR)),
                BandMember(id = "2", userId = null, nickname = "Jane", roles = listOf(Role.DRUMS))
            )
        ),
        eventsFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToBandDetail = {}
    )
}
