package com.amolieres.setlistync.core.domain.band.repository

import com.amolieres.setlistync.core.domain.band.model.Gig
import kotlinx.coroutines.flow.Flow

interface GigRepository {
    suspend fun addGig(gig: Gig)
    suspend fun getGig(gigId: String): Gig?
    suspend fun getGigsByBandId(bandId: String): List<Gig>
    fun observeGigsByBandId(bandId: String): Flow<List<Gig>>
    suspend fun updateGig(gig: Gig)
    suspend fun deleteGig(gigId: String)
    suspend fun deleteGigsByBandId(bandId: String)
}
