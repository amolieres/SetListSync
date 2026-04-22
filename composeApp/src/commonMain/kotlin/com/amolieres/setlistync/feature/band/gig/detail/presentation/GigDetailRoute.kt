package com.amolieres.setlistync.feature.band.gig.detail.presentation

import kotlinx.serialization.Serializable

@Serializable data class GigDetailRoute(val bandId: String, val gigId: String, val isEditing: Boolean = false)
