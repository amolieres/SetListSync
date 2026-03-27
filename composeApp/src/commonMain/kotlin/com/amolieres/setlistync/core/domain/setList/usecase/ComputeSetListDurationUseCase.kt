package com.amolieres.setlistync.core.domain.setList.usecase

import com.amolieres.setlistync.core.domain.setList.model.SetList
import com.amolieres.setlistync.core.domain.song.repository.SongRepository

/**
 * Returns the total duration in seconds of all songs in the setlist.
 * Songs that can't be found (e.g. deleted) are counted as 0.
 */
class ComputeSetListDurationUseCase(private val songRepo: SongRepository) {
    suspend operator fun invoke(setList: SetList): Int =
        setList.orderedSongIds.sumOf { songId ->
            songRepo.getSong(songId)?.durationSeconds ?: 0
        }
}
