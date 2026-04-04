package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlin.time.ExperimentalTime

class AddSongToGigUseCase(private val repo: GigRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(gigId: String, songId: SongId): Result<Unit> = runCatching {
        val gig = repo.getGig(gigId) ?: error("Gig not found: $gigId")
        if (songId in gig.orderedSongIds) return@runCatching
        repo.updateGig(gig.copy(orderedSongIds = gig.orderedSongIds + songId))
    }
}
