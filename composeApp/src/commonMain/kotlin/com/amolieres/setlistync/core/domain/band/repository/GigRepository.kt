package com.amolieres.setlistync.core.domain.band.repository

import com.amolieres.setlistync.core.domain.band.model.Gig

interface GigRepository {
    suspend fun addGig(gig: Gig)
    suspend fun getGig(gigId: String): Gig?
    suspend fun getGigsByBandId(bandId: String): List<Gig>
    suspend fun updateGig(gig: Gig)
    suspend fun deleteGig(gigId: String)
    suspend fun deleteGigsByBandId(bandId: String)
}
