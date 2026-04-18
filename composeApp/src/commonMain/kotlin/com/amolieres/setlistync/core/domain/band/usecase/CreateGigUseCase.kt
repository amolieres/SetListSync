package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
    suspend operator fun invoke(
        bandId: String,
        venue: String? = null,
        date: Instant? = null,
        expectedDurationMinutes: Int? = null,
        orderedSongIds: List<SongId> = emptyList()
    ): Result<Gig> = runCatching {
        val gig = Gig(
            id = Uuid.random().toString(),
            bandId = bandId,
            venue = venue?.trim(),
            date = date,
            expectedDurationMinutes = expectedDurationMinutes,
            orderedSongIds = orderedSongIds
        )
        repo.addGig(gig)
        gig
    }
}
