package com.amolieres.setlistync.core.data.local.dao

import androidx.room.*
import com.amolieres.setlistync.core.data.local.entity.SongNoteEntity

@Dao
interface SongNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: SongNoteEntity)

    @Query("SELECT * FROM song_notes WHERE id = :id")
    suspend fun getNoteById(id: String): SongNoteEntity?

    @Query("SELECT * FROM song_notes WHERE memberId = :memberId")
    suspend fun getNotesByMemberId(memberId: String): List<SongNoteEntity>

    @Query("SELECT * FROM song_notes WHERE songId = :songId")
    suspend fun getNotesBySongId(songId: String): List<SongNoteEntity>

    @Query("SELECT * FROM song_notes WHERE memberId = :memberId AND songId = :songId LIMIT 1")
    suspend fun getNoteByMemberAndSong(memberId: String, songId: String): SongNoteEntity?

    @Update
    suspend fun updateNote(note: SongNoteEntity)

    @Query("DELETE FROM song_notes WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("DELETE FROM song_notes WHERE songId = :songId")
    suspend fun deleteNotesBySongId(songId: String)
}
