package com.amolieres.setlistync.core.domain.band.model

data class BandPresenter(
    val bandId: String,
    val bandName: String,
    val memberCount: Int,
    val songCount: Int,
    val gigCount: Int = 0
)
