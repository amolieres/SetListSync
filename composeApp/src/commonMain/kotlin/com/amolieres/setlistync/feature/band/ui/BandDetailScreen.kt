package com.amolieres.setlistync.feature.band.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import com.amolieres.setlistync.feature.band.presentation.BandDetailEvent
import com.amolieres.setlistync.feature.band.presentation.BandDetailUiEvent
import com.amolieres.setlistync.feature.band.presentation.BandDetailUiState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandDetailScreen(
    uiState: BandDetailUiState,
    eventFlow: Flow<BandDetailEvent>,
    onScreenEvent: (BandDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandDetailEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.band?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onScreenEvent(BandDetailUiEvent.OnDeleteBandClicked) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete band")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onScreenEvent(BandDetailUiEvent.OnAddMemberClicked) }) {
                Icon(Icons.Default.Add, contentDescription = "Add member")
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            uiState.band == null -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Band not found.") }

            uiState.band.members.isEmpty() -> Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("No members yet. Tap + to add one.") }

            else -> LazyColumn(
                Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                items(uiState.band.members) { member ->
                    MemberListItem(
                        member = member,
                        onEdit = { onScreenEvent(BandDetailUiEvent.OnEditMemberClicked(member)) },
                        onDelete = { onScreenEvent(BandDetailUiEvent.OnDeleteMemberClicked(member.id)) }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    // Member add/edit dialog
    if (uiState.showMemberDialog) {
        MemberDialog(
            title = if (uiState.editingMember != null) "Edit member" else "Add member",
            nickname = uiState.memberNickname,
            selectedRoles = uiState.memberRoles,
            onNicknameChanged = { onScreenEvent(BandDetailUiEvent.OnMemberNicknameChanged(it)) },
            onRoleToggled = { onScreenEvent(BandDetailUiEvent.OnMemberRoleToggled(it)) },
            onConfirm = { onScreenEvent(BandDetailUiEvent.OnMemberDialogConfirmed) },
            onDismiss = { onScreenEvent(BandDetailUiEvent.OnMemberDialogDismiss) }
        )
    }

    // Delete band confirmation dialog
    if (uiState.showDeleteBandConfirm) {
        AlertDialog(
            onDismissRequest = { onScreenEvent(BandDetailUiEvent.OnDeleteBandDismiss) },
            title = { Text("Delete band") },
            text = { Text("Are you sure you want to delete \"${uiState.band?.name}\" and all its members?") },
            confirmButton = {
                TextButton(onClick = { onScreenEvent(BandDetailUiEvent.OnDeleteBandConfirmed) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(BandDetailUiEvent.OnDeleteBandDismiss) }) { Text("Cancel") }
            }
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
        headlineContent = {
            Text(member.nickname ?: "Member")
        },
        supportingContent = {
            if (member.roles.isNotEmpty()) {
                Text(member.roles.joinToString(" · ") { it.name.lowercase().replaceFirstChar { c -> c.uppercase() } })
            }
        },
        trailingContent = {
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    )
}

@Composable
private fun MemberDialog(
    title: String,
    nickname: String,
    selectedRoles: Set<Role>,
    onNicknameChanged: (String) -> Unit,
    onRoleToggled: (Role) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = onNicknameChanged,
                    label = { Text("Nickname (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("Roles", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Role.entries.forEach { role ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = role in selectedRoles,
                            onCheckedChange = { onRoleToggled(role) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(role.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
