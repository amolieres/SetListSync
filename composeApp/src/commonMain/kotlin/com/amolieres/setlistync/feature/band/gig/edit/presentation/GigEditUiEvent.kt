package com.amolieres.setlistync.feature.band.gig.edit.presentation

sealed interface GigEditUiEvent {
    data class OnVenueChanged(val venue: String) : GigEditUiEvent
    data class OnExpectedDurationChanged(val minutes: String) : GigEditUiEvent
    data object OnDatePickerOpen : GigEditUiEvent
    data object OnDatePickerDismissed : GigEditUiEvent
    data class OnDateSelected(val epochMillis: Long?) : GigEditUiEvent
    data object OnSaveClicked : GigEditUiEvent
    data object OnImportClicked : GigEditUiEvent
    data object OnImportDismissed : GigEditUiEvent
    data class OnImportGigSelected(val gigId: String) : GigEditUiEvent
}
