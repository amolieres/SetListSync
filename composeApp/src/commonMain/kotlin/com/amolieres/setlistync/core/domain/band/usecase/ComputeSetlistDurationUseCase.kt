package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.song.model.Song

/** Returns the total setlist duration in seconds by summing the duration of each song. */
class ComputeSetlistDurationUseCase {
    operator fun invoke(songs: List<Song>): Int = songs.sumOf { it.durationSeconds }
}
