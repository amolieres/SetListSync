package com.amolieres.setlistync.core.data.song

import com.amolieres.setlistync.core.data.local.dao.SongNoteDao
import com.amolieres.setlistync.core.data.local.entity.SongNoteEntity
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongNote
import com.amolieres.setlistync.core.domain.song.repository.SongNoteRepository

class SongNoteRepositoryImpl(
    private val songNoteDao: SongNoteDao
) : SongNoteRepository {

    override suspend fun addNote(note: SongNote) {
        songNoteDao.insertNote(note.toEntity())
    }

    override suspend fun getNotesByMemberId(memberId: String): List<SongNote> =
        songNoteDao.getNotesByMemberId(memberId).map { it.toDomain() }

    override suspend fun getNotesBySongId(songId: SongId): List<SongNote> =
        songNoteDao.getNotesBySongId(songId.value).map { it.toDomain() }

    override suspend fun getNoteByMemberAndSong(memberId: String, songId: SongId): SongNote? =
        songNoteDao.getNoteByMemberAndSong(memberId, songId.value)?.toDomain()

    override suspend fun updateNote(note: SongNote) {
        songNoteDao.updateNote(note.toEntity())
    }

    override suspend fun deleteNote(noteId: String) {
        songNoteDao.deleteNote(noteId)
    }

    override suspend fun deleteNotesBySongId(songId: SongId) {
        songNoteDao.deleteNotesBySongId(songId.value)
    }

    // --- Mappers ---

    private fun SongNote.toEntity() = SongNoteEntity(
        id = id,
        songId = songId.value,
        memberId = memberId,
        note = note
    )

    private fun SongNoteEntity.toDomain() = SongNote(
        id = id,
        songId = SongId(songId),
        memberId = memberId,
        note = note
    )
}
