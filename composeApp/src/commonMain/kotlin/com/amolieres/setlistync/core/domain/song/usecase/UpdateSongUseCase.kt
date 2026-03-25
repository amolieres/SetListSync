package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.repository.SongRepository

class UpdateSongUseCase(private val repo: SongRepository) {
    suspend operator fun invoke(bandId: String, song: Song) = repo.updateSong(bandId, song)
}
