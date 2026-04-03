package com.amolieres.setlistync.fake

import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongNote
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository

/**
 * No-op implementation of [SongNoteRepository] for tests that don't need note behaviour.
 */
class FakeSongNoteRepository : SongNoteRepository {
    override suspend fun addNote(note: SongNote) {}
    override suspend fun getNotesByMemberId(memberId: String): List<SongNote> = emptyList()
    override suspend fun getNotesBySongId(songId: SongId): List<SongNote> = emptyList()
    override suspend fun getNoteByMemberAndSong(memberId: String, songId: SongId): SongNote? = null
    override suspend fun updateNote(note: SongNote) {}
    override suspend fun deleteNote(noteId: String) {}
    override suspend fun deleteNotesBySongId(songId: SongId) {}
}
