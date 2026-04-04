package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlin.time.ExperimentalTime

/**
 * Replaces the ordered song list of a gig with [newOrder].
 * [newOrder] must contain the same song IDs as the current list.
 */
class ReorderGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(gigId: String, newOrder: List<SongId>): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        require(newOrder.toSet() == gig.orderedSongIds.toSet()) {
            "newOrder must contain exactly the same song IDs as the current setlist"
        }
        repo.updateGig(gig.copy(orderedSongIds = newOrder))
    }
}
