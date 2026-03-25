package com.amolieres.setlistync.core.data.band

import com.amolieres.setlistync.core.data.local.dao.GigDao
import com.amolieres.setlistync.core.data.local.entity.GigEntity
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class GigRepositoryImpl(
    private val gigDao: GigDao
) : GigRepository {

    override suspend fun addGig(gig: Gig) {
        gigDao.insertGig(gig.toEntity())
    }

    override suspend fun getGig(gigId: String): Gig? =
        gigDao.getGigById(gigId)?.toDomain()

    override suspend fun getGigsByBandId(bandId: String): List<Gig> =
        gigDao.getGigsByBandId(bandId).map { it.toDomain() }

    override suspend fun updateGig(gig: Gig) {
        gigDao.updateGig(gig.toEntity())
    }

    override suspend fun deleteGig(gigId: String) {
        gigDao.deleteGig(gigId)
    }

    override suspend fun deleteGigsByBandId(bandId: String) {
        gigDao.deleteGigsByBandId(bandId)
    }

    // --- Mappers ---

    @OptIn(ExperimentalTime::class)
    private fun Gig.toEntity() = GigEntity(
        id = id,
        bandId = bandId,
        venue = venue,
        dateEpochMs = date?.let { it.epochSeconds * 1000L + it.nanosecondsOfSecond / 1_000_000 },
        expectedDurationMinutes = expectedDurationMinutes,
        setListIds = setListIds?.let { Json.encodeToString(it) }
    )

    @OptIn(ExperimentalTime::class)
    private fun GigEntity.toDomain() = Gig(
        id = id,
        bandId = bandId,
        venue = venue,
        date = dateEpochMs?.let { Instant.fromEpochMilliseconds(it) },
        expectedDurationMinutes = expectedDurationMinutes,
        setListIds = setListIds?.let { Json.decodeFromString(it) }
    )
}
