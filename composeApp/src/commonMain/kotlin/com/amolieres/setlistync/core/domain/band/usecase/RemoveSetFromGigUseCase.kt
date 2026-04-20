package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import kotlin.time.ExperimentalTime

/** Removes a set from the gig. */
class RemoveSetFromGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(gigId: String, setId: String): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        repo.updateGig(gig.copy(sets = gig.sets.filter { it.id != setId }))
    }
}
