package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository

class DeleteSongNoteUseCase(private val repo: SongNoteRepository) {
    suspend operator fun invoke(noteId: String) = repo.deleteNote(noteId)
}
