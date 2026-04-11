package com.amolieres.setlistync.feature.band.gig.edit.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.amolieres.setlistync.app.designsystem.AppDimens
import com.amolieres.setlistync.app.designsystem.components.AppCenteredLoader
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditEvent
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditUiEvent
import com.amolieres.setlistync.feature.band.gig.edit.presentation.GigEditUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.jetbrains.compose.resources.stringResource
import setlistsync.composeapp.generated.resources.Res
import setlistsync.composeapp.generated.resources.action_back
import setlistsync.composeapp.generated.resources.action_cancel
import setlistsync.composeapp.generated.resources.action_save
import setlistsync.composeapp.generated.resources.gig_detail_title_create
import setlistsync.composeapp.generated.resources.gig_detail_title_edit
import setlistsync.composeapp.generated.resources.gig_edit_cd_delete
import setlistsync.composeapp.generated.resources.gig_field_date_none
import setlistsync.composeapp.generated.resources.gig_field_expected_duration
import setlistsync.composeapp.generated.resources.gig_field_venue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun GigEditScreen(
    uiState: GigEditUiState,
    eventFlow: Flow<GigEditEvent>,
    onScreenEvent: (GigEditUiEvent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToGigDetail: (String) -> Unit = {},
    onNavigateBackToBandDetail: () -> Unit = {},
    isEditMode: Boolean = false
) {
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                GigEditEvent.NavigateBack -> onNavigateBack()
                is GigEditEvent.NavigateToGigDetail -> onNavigateToGigDetail(event.gigId)
                GigEditEvent.NavigateBackToBandDetail -> onNavigateBackToBandDetail()
            }
        }
    }

    if (uiState.showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.dateMillis
        )
        DatePickerDialog(
            onDismissRequest = { onScreenEvent(GigEditUiEvent.OnDatePickerDismissed) },
            confirmButton = {
                TextButton(onClick = {
                    onScreenEvent(GigEditUiEvent.OnDateSelected(datePickerState.selectedDateMillis))
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { onScreenEvent(GigEditUiEvent.OnDatePickerDismissed) }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (isEditMode) Res.string.gig_detail_title_edit
                            else Res.string.gig_detail_title_create
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.action_back)
                        )
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { onScreenEvent(GigEditUiEvent.OnDeleteClicked) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(Res.string.gig_edit_cd_delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            AppCenteredLoader(Modifier.padding(padding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(AppDimens.SpacingL),
            verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingM)
        ) {
            // ── Venue ──────────────────────────────────────────────────────
            OutlinedTextField(
                value = uiState.venueInput,
                onValueChange = { onScreenEvent(GigEditUiEvent.OnVenueChanged(it)) },
                label = { Text(stringResource(Res.string.gig_field_venue)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ── Date ───────────────────────────────────────────────────────
            OutlinedButton(
                onClick = { onScreenEvent(GigEditUiEvent.OnDatePickerOpen) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(AppDimens.IconSizeSmall)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(
                    uiState.dateMillis?.let { ms ->
                        Instant.fromEpochMilliseconds(ms).toString().take(10)
                    } ?: stringResource(Res.string.gig_field_date_none)
                )
            }

            // ── Expected duration ──────────────────────────────────────────
            OutlinedTextField(
                value = uiState.expectedDurationInput,
                onValueChange = { onScreenEvent(GigEditUiEvent.OnExpectedDurationChanged(it)) },
                label = { Text(stringResource(Res.string.gig_field_expected_duration)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.weight(1f))

            // ── Save button ────────────────────────────────────────────────
            Button(
                onClick = { onScreenEvent(GigEditUiEvent.OnSaveClicked) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(AppDimens.IconSizeSmall),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = AppDimens.SpacingXxs
                    )
                } else {
                    Text(stringResource(Res.string.action_save))
                }
            }
        }
    }
}

@Preview
@Composable
private fun GigEditScreenCreatePreview() {
    GigEditScreen(
        uiState = GigEditUiState(),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        isEditMode = false
    )
}

@Preview
@Composable
private fun GigEditScreenEditPreview() {
    GigEditScreen(
        uiState = GigEditUiState(venueInput = "Zénith Paris", expectedDurationInput = "90"),
        eventFlow = emptyFlow(),
        onScreenEvent = {},
        onNavigateBack = {},
        isEditMode = true
    )
}
