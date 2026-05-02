package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.GigSet
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/** Adds a new empty set to the gig. */
class AddSetToGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    suspend operator fun invoke(gigId: String): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        val newSet = GigSet(id = "set_${Uuid.random()}")
        repo.updateGig(gig.copy(sets = gig.sets + newSet))
    }
}
