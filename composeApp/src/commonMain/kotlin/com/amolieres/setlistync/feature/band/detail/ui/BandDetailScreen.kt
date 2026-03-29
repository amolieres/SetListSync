package com.amolieres.setlistync.feature.band.detail.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.app.designsystem.components.AppInfoRow
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailEvent
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailUiEvent
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailUiState
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.*
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BandDetailScreen(
    uiState: BandDetailUiState,
    eventFlow: Flow<BandDetailEvent>,
    onScreenEvent: (BandDetailUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToMembers: () -> Unit,
    onNavigateToEdit: () -> Unit = {}
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandDetailEvent.NavigateBack -> onNavigateBack()
                BandDetailEvent.NavigateToMembers -> onNavigateToMembers()
                BandDetailEvent.NavigateToEdit -> onNavigateToEdit()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.band?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.action_back))
                    }
                },
                actions = {
                    IconButton(onClick = { onScreenEvent(BandDetailUiEvent.OnDeleteBandClicked) }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.band_detail_cd_delete))
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> AppCenteredLoader(Modifier.padding(padding))

            uiState.band == null -> AppCenteredMessage(
                text = stringResource(Res.string.band_detail_not_found),
                modifier = Modifier.padding(padding)
            )

            else -> Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                BandInfoSection(
                    band = uiState.band,
                    onEditClicked = { onScreenEvent(BandDetailUiEvent.OnEditInfoClicked) }
                )
                HorizontalDivider()

                val memberCount = uiState.band.members.size
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.band_detail_section_members)) },
                    trailingContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("$memberCount ${stringResource(if (memberCount == 1) Res.string.member_singular else Res.string.member_plural)}")
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    },
                    modifier = Modifier.clickable { onScreenEvent(BandDetailUiEvent.OnMembersSectionClicked) }
                )
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text(stringResource(Res.string.band_detail_section_setlists)) },
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

// ── Info section (read-only) ─────────────────────────────────────────────────

@Composable
private fun BandInfoSection(band: Band, onEditClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppDimens.SpacingL, vertical = AppDimens.SpacingM)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(Res.string.band_detail_section_info), style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = onEditClicked) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(Res.string.band_detail_cd_edit_info))
            }
        }

        Spacer(Modifier.height(AppDimens.SpacingS))

        val hasAnyInfo = band.email != null
            || band.instagramUrl != null
            || band.facebookUrl != null
            || band.tiktokUrl != null
            || band.genres.isNotEmpty()

        if (!hasAnyInfo) {
            Text(
                stringResource(Res.string.band_detail_no_info),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return
        }

        Column(verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingXs)) {
            if (band.genres.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(AppDimens.IconSizeMedium).padding(top = AppDimens.SpacingXxs),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(AppDimens.SpacingXxs)) {
                        band.genres.forEach { genre ->
                            SuggestionChip(onClick = {}, label = { Text(genre) })
                        }
                    }
                }
            }
            band.email?.let { AppInfoRow(icon = Icons.Default.Email, text = it) }
            band.instagramUrl?.let { AppInfoRow(icon = Icons.Default.Share, text = it, label = "Instagram") }
            band.facebookUrl?.let { AppInfoRow(icon = Icons.Default.Share, text = it, label = "Facebook") }
            band.tiktokUrl?.let { AppInfoRow(icon = Icons.Default.Share, text = it, label = "TikTok") }
        }
    }
}
