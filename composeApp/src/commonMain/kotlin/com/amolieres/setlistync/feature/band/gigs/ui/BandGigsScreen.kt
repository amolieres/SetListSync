package com.amolieres.setlistync.feature.band.gigs.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.app.designsystem.components.AppCenteredMessage
import com.amolieres.setlistync.feature.band.gigs.presentation.BandGigsEvent
import com.amolieres.setlistync.feature.band.gigs.presentation.BandGigsUiEvent
import com.amolieres.setlistync.feature.band.gigs.presentation.BandGigsUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.action_back
import setlistsync.composeapp.generated.resources.band_gigs_empty
import setlistsync.composeapp.generated.resources.band_gigs_fab_add
import setlistsync.composeapp.generated.resources.band_detail_section_gigs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandGigsScreen(
    uiState: BandGigsUiState,
    eventFlow: Flow<BandGigsEvent>,
    onScreenEvent: (BandGigsUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToNewGig: () -> Unit,
    onNavigateToGigDetail: (String) -> Unit
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                BandGigsEvent.NavigateBack -> onNavigateBack()
                BandGigsEvent.NavigateToNewGig -> onNavigateToNewGig()
                is BandGigsEvent.NavigateToGigDetail -> onNavigateToGigDetail(event.gigId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.bandName) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onScreenEvent(BandGigsUiEvent.OnAddGigClicked) }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.band_gigs_fab_add))
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> AppCenteredLoader(Modifier.padding(padding))

            uiState.gigs.isEmpty() -> AppCenteredMessage(
                text = stringResource(Res.string.band_gigs_empty),
                modifier = Modifier.padding(padding)
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    horizontal = AppDimens.SpacingL,
                    vertical = AppDimens.SpacingM
                ),
                verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingS)
            ) {
                items(uiState.gigs, key = { it.id }) { gig ->
                    GigItem(
                        gig = gig,
                        onEdit = { onScreenEvent(BandGigsUiEvent.OnGigClicked(gig.id)) },
                        onDelete = { onScreenEvent(BandGigsUiEvent.OnDeleteGigClicked(gig.id)) }
                    )
                }
                item { Spacer(Modifier.height(AppDimens.FabSpacing)) }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview
@Composable
private fun BandGigsScreenLoadingPreview() {
    BandGigsScreen(
        uiState = BandGigsUiState(isLoading = true, bandName = "The Rocketeers"),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToNewGig = {},
        onNavigateToGigDetail = {}
    )
}

@Preview
@Composable
private fun BandGigsScreenEmptyPreview() {
    BandGigsScreen(
        uiState = BandGigsUiState(isLoading = false, bandName = "The Rocketeers"),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        onNavigateToNewGig = {},
        onNavigateToGigDetail = {}
    )
}
