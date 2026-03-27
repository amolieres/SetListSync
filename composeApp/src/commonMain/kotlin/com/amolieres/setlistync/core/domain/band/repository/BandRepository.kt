package com.amolieres.setlistync.core.domain.band.repository

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import kotlinx.coroutines.flow.Flow

interface BandRepository {
    // Band CRUD
    suspend fun createBand(band: Band)
    suspend fun getBand(bandId: String): Band?
    suspend fun getAllBands(): List<Band>
    suspend fun updateBand(band: Band)
    suspend fun deleteBand(bandId: String)
    suspend fun deleteAllBands()

    // Reactive observation
    fun observeAllBands(): Flow<List<Band>>
    fun observeBand(bandId: String): Flow<Band?>

    // Member operations (band-specific)
    suspend fun addMemberToBand(bandId: String, member: BandMember)
    suspend fun removeMemberFromBand(bandId: String, memberId: String)
    suspend fun updateMemberInBand(bandId: String, member: BandMember)

    // Queries
    suspend fun getBandsForUser(userId: String): List<Band>
}