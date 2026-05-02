package com.amolieres.setlistync.fake

import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGigRepository : GigRepository {

    private val gigs = mutableMapOf<String, Gig>()
    val gigsFlow = MutableStateFlow<List<Gig>>(emptyList())

    var deletedGigId: String? = null

    override suspend fun addGig(gig: Gig) {
        gigs[gig.id] = gig
        gigsFlow.value = gigs.values.toList()
    }

    override suspend fun getGig(gigId: String): Gig? = gigs[gigId]

    override suspend fun getGigsByBandId(bandId: String): List<Gig> =
        gigs.values.filter { it.bandId == bandId }

    override fun observeGigsByBandId(bandId: String): Flow<List<Gig>> = gigsFlow

    override suspend fun updateGig(gig: Gig) {
        gigs[gig.id] = gig
        gigsFlow.value = gigs.values.toList()
    }

    override suspend fun deleteGig(gigId: String) {
        deletedGigId = gigId
        gigs.remove(gigId)
        gigsFlow.value = gigs.values.toList()
    }

    override suspend fun deleteGigsByBandId(bandId: String) {
        gigs.entries.removeAll { it.value.bandId == bandId }
        gigsFlow.value = gigs.values.toList()
    }
}
