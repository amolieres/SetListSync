package com.amolieres.setlistync.feature.band.members.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersUiEvent
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersUiState
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import com.amolieres.setlistync.core.domain.band.model.Role
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandMembersScreen(
    uiState: BandMembersUiState,
    onScreenEvent: (BandMembersUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.band_members_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onScreenEvent(BandMembersUiEvent.OnAddMemberClicked) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.band_members_cd_add))
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> AppCenteredLoader(Modifier.padding(padding))

            uiState.members.isEmpty() -> AppCenteredMessage(
                text = stringResource(Res.string.band_members_empty),
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = AppDimens.FabSpacing)
            ) {
                items(uiState.members) { member ->
                    MemberListItem(
                        member = member,
                        onEdit = { onScreenEvent(BandMembersUiEvent.OnEditMemberClicked(member)) },
                        onDelete = { onScreenEvent(BandMembersUiEvent.OnDeleteMemberClicked(member.id)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    if (uiState.showMemberDialog) {
        MemberDialog(
            title = if (uiState.editingMember != null) stringResource(Res.string.member_dialog_edit_title) else stringResource(Res.string.member_dialog_add_title),
            nickname = uiState.memberNickname,
            selectedRoles = uiState.memberRoles,
            onNicknameChanged = { onScreenEvent(BandMembersUiEvent.OnMemberNicknameChanged(it)) },
            onRoleToggled = { onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(it)) },
            onConfirm = { onScreenEvent(BandMembersUiEvent.OnMemberDialogConfirmed) },
            onDismiss = { onScreenEvent(BandMembersUiEvent.OnMemberDialogDismiss) }
        )
    }
}

@Composable
private fun MemberListItem(
    member: BandMember,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        headlineContent = { Text(member.nickname ?: stringResource(Res.string.band_members_default_name)) },
        supportingContent = {
            if (member.roles.isNotEmpty()) {
                Text(member.roles.joinToString(" · ") { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } })
            }
        },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.band_members_cd_edit))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.band_members_cd_delete))
                }
            }
        }
    )
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
fun BandMembersScreenLoadingPreview() {
    BandMembersScreen(
        uiState = BandMembersUiState(isLoading = true),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun BandMembersScreenEmptyPreview() {
    BandMembersScreen(
        uiState = BandMembersUiState(isLoading = false, members = emptyList()),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}

@Preview
@Composable
fun BandMembersScreenContentPreview() {
    BandMembersScreen(
        uiState = BandMembersUiState(
            isLoading = false,
            members = listOf(
                BandMember(id = "1", userId = null, nickname = "John", roles = listOf(Role.VOCALS, Role.GUITAR)),
                BandMember(id = "2", userId = null, nickname = "Jane", roles = listOf(Role.DRUMS)),
                BandMember(id = "3", userId = null, nickname = "Bob", roles = listOf(Role.BASS))
            )
        ),
        onScreenEvent = {},
        onNavigateBack = {}
    )
}
