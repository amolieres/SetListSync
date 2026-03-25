package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongNote
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository

class GetNotesForSongUseCase(private val repo: SongNoteRepository) {
    suspend operator fun invoke(songId: SongId): List<SongNote> = repo.getNotesBySongId(songId)
}
