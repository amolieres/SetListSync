package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlin.time.ExperimentalTime

class AddSongToGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(gigId: String, setId: String, songId: SongId): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        val updatedSets = gig.sets.map { set ->
            if (set.id == setId && songId !in set.orderedSongIds) {
                set.copy(orderedSongIds = set.orderedSongIds + songId)
            } else set
        }
        repo.updateGig(gig.copy(sets = updatedSets))
    }
}
