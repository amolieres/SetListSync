package com.amolieres.setlistync.core.domain.song.repository

import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.model.SongNote

interface SongNoteRepository {
    suspend fun addNote(note: SongNote)
    suspend fun getNotesByMemberId(memberId: String): List<SongNote>
    suspend fun getNotesBySongId(songId: SongId): List<SongNote>
    suspend fun getNoteByMemberAndSong(memberId: String, songId: SongId): SongNote?
    suspend fun updateNote(note: SongNote)
    suspend fun deleteNote(noteId: String)
    suspend fun deleteNotesBySongId(songId: SongId)
}
