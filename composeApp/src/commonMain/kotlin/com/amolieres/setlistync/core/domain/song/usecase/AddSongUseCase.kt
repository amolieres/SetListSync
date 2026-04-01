package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddSongUseCase(private val repo: SongRepository) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        bandId: String,
        title: String,
        durationSeconds: Int,
        key: String? = null,
        tempo: Int? = null,
        externalLinks: List<String> = emptyList(),
        originalArtist: String? = null
    ): Result<Song> = runCatching {
        val song = Song(
            id = SongId(Uuid.random().toString()),
            title = title.trim(),
            durationSeconds = durationSeconds,
            key = key?.trim(),
            tempo = tempo,
            externalLinks = externalLinks,
            originalArtist = originalArtist?.trim()
        )
        repo.addSong(bandId, song)
        song
    }
}
