package com.amolieres.setlistync.core.data.band

import com.amolieres.setlistync.core.data.local.dao.GigDao
import com.amolieres.setlistync.core.data.local.entity.GigEntity
import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override fun observeGigsByBandId(bandId: String): Flow<List<Gig>> =
        gigDao.observeGigsByBandId(bandId).map { list -> list.map { it.toDomain() } }

    override suspend fun updateGig(gig: Gig) {
        gigDao.updateGig(gig.toEntity())
    }

    override suspend fun deleteGig(gigId: String) {
        gigDao.deleteGig(gigId)
    }

    override suspend fun deleteGigsByBandId(bandId: String) {
        gigDao.deleteGigsByBandId(bandId)
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    @OptIn(ExperimentalTime::class)
    private fun Gig.toEntity() = GigEntity(
        id = id,
        bandId = bandId,
        venue = venue,
        dateEpochMs = date?.let { it.epochSeconds * 1000L + it.nanosecondsOfSecond / 1_000_000 },
        expectedDurationMinutes = expectedDurationMinutes,
        orderedSongIds = Json.encodeToString(orderedSongIds.map { it.value })
    )

    @OptIn(ExperimentalTime::class)
    private fun GigEntity.toDomain() = Gig(
        id = id,
        bandId = bandId,
        venue = venue,
        date = dateEpochMs?.let { Instant.fromEpochMilliseconds(it) },
        expectedDurationMinutes = expectedDurationMinutes,
        orderedSongIds = Json.decodeFromString<List<String>>(orderedSongIds).map { SongId(it) }
    )
}
