package com.amolieres.setlistync.feature.band.detail.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailEvent
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailUiEvent
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailUiState
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandDetailScreen(
    uiState: BandDetailUiState,
    eventFlow: Flow<BandDetailEvent>,
    onScreenEvent: (BandDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMembers: () -> Unit
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandDetailEvent.NavigateBack -> onNavigateBack()
                BandDetailEvent.NavigateToMembers -> onNavigateToMembers()
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

            else -> Column(Modifier.fillMaxSize().padding(padding)) {
                val memberCount = uiState.band.members.size
                ListItem(
                    headlineContent = { Text("Members") },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$memberCount member${if (memberCount != 1) "s" else ""}")
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    },
                    modifier = Modifier.clickable { onScreenEvent(BandDetailUiEvent.OnMembersSectionClicked) }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text("Set Lists") },
                    trailingContent = {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    },
                    colors = ListItemDefaults.colors(
                        headlineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                )
                HorizontalDivider()
            }
        }
    }

    if (uiState.showDeleteBandConfirm) {
        DeleteBandDialog(
            bandName = uiState.band?.name,
            onConfirm = { onScreenEvent(BandDetailUiEvent.OnDeleteBandConfirmed) },
            onDismiss = { onScreenEvent(BandDetailUiEvent.OnDeleteBandDismiss) }
        )
    }
}
