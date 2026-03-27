package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.repository.SongRepository

class GetSongUseCase(private val repo: SongRepository) {
    suspend operator fun invoke(id: SongId): Song? = repo.getSong(id)
}
