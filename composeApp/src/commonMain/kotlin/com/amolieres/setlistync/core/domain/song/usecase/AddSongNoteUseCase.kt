package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongNote
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Upsert: updates the existing note if one already exists for (memberId, songId),
 * otherwise creates a new one.
 */
class AddSongNoteUseCase(private val repo: SongNoteRepository) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        memberId: String,
        songId: SongId,
        note: String
    ): Result<SongNote> = runCatching {
        val existing = repo.getNoteByMemberAndSong(memberId, songId)
        if (existing != null) {
            val updated = existing.copy(note = note)
            repo.updateNote(updated)
            updated
        } else {
            val newNote = SongNote(
                id = Uuid.random().toString(),
                songId = songId,
                memberId = memberId,
                note = note
            )
            repo.addNote(newNote)
            newNote
        }
    }
}
