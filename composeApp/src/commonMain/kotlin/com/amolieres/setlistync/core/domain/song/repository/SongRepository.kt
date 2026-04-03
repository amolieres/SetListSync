package com.amolieres.setlistync.core.domain.song.repository

import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlinx.coroutines.flow.Flow

interface SongRepository {
    suspend fun getAllSongs(bandId: String): List<Song>
    fun observeAllSongs(bandId: String): Flow<List<Song>>
    suspend fun getSong(id: SongId): Song?
    suspend fun addSong(bandId: String, song: Song)
    suspend fun updateSong(bandId: String, song: Song)
    suspend fun deleteSong(bandId: String, id: SongId)
}