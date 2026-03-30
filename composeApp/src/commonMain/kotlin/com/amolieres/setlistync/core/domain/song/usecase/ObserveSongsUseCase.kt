package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import kotlinx.coroutines.flow.Flow

class ObserveSongsUseCase(private val repo: SongRepository) {
    operator fun invoke(bandId: String): Flow<List<Song>> = repo.observeAllSongs(bandId)
}
