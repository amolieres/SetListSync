package com.amolieres.setlistync.core.domain.repository

import com.amolieres.setlistync.core.domain.model.Song
import com.amolieres.setlistync.core.domain.model.SongId

interface SongRepository {
    suspend fun getAllSongs(bandId: String): List<Song>
    suspend fun getSong(id: SongId): Song?
    suspend fun addSong(bandId: String, song: Song)
    suspend fun updateSong(bandId: String, song: Song)
    suspend fun deleteSong(bandId: String, id: SongId)
}