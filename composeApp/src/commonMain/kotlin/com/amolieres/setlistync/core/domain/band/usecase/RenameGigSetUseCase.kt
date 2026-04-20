package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import kotlin.time.ExperimentalTime

/** Renames a set within a gig. Pass null [title] to clear the title. */
class RenameGigSetUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(gigId: String, setId: String, title: String?): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        val updatedSets = gig.sets.map { set ->
            if (set.id == setId) set.copy(title = title?.trim()?.ifEmpty { null }) else set
        }
        repo.updateGig(gig.copy(sets = updatedSets))
    }
}
