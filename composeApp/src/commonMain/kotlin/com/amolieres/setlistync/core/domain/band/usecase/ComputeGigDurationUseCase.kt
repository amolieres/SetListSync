package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.repository.SongRepository

/** Returns the total duration in seconds for all songs across all sets in a gig. */
class ComputeGigDurationUseCase(
    private val gigRepo: GigRepository,
    private val songRepo: SongRepository
) {
    suspend operator fun invoke(gigId: String): Result<Int> = runCatching {
        val gig = gigRepo.getGig(gigId) ?: error("Gig not found: $gigId")
        gig.sets.sumOf { set ->
            set.orderedSongIds.sumOf { songId ->
                songRepo.getSong(songId)?.durationSeconds ?: 0
            }
        }
    }
}
