package com.amolieres.setlistync.core.domain.repository

import com.amolieres.setlistync.core.domain.model.Band

interface BandRepository {
    suspend fun getBands(): List<Band>
    suspend fun getBand(id: String): Band?
    suspend fun addBand(band: Band)
    suspend fun updateBand(band: Band)
}