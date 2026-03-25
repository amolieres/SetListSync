package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository
import com.amolieres.setlistync.core.domain.song.repository.SongRepository

class DeleteSongUseCase(
    private val songRepo: SongRepository,
    private val noteRepo: SongNoteRepository
) {
    suspend operator fun invoke(bandId: String, songId: SongId) {
        noteRepo.deleteNotesBySongId(songId)
        songRepo.deleteSong(bandId, songId)
    }
}
