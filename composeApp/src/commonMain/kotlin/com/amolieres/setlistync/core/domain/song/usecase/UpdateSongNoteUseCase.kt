package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.SongNote
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository

class UpdateSongNoteUseCase(private val repo: SongNoteRepository) {
    suspend operator fun invoke(note: SongNote) = repo.updateNote(note)
}
