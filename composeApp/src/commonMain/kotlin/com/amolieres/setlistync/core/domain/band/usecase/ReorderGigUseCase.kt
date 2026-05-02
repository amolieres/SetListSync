package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlin.time.ExperimentalTime

/**
 * Replaces the ordered song list of a specific set within a gig with [newOrder].
 * [newOrder] must contain the same song IDs as the current list for that set.
 */
class ReorderGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(gigId: String, setId: String, newOrder: List<SongId>): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        val updatedSets = gig.sets.map { set ->
            if (set.id == setId) {
                require(newOrder.toSet() == set.orderedSongIds.toSet()) {
                    "newOrder must contain exactly the same song IDs as the current set"
                }
                set.copy(orderedSongIds = newOrder)
            } else set
        }
        repo.updateGig(gig.copy(sets = updatedSets))
    }
}
