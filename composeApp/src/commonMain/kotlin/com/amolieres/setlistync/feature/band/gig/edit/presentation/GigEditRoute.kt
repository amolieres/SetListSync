package com.amolieres.setlistync.feature.band.gig.edit.presentation

import kotlinx.serialization.Serializable

@Serializable data class GigEditRoute(val bandId: String, val gigId: String? = null)
