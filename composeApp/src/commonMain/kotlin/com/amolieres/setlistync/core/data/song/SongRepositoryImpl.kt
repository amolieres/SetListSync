package com.amolieres.setlistync.core.data.song

import com.amolieres.setlistync.core.data.local.dao.SongDao
import com.amolieres.setlistync.core.data.local.entity.SongEntity
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SongRepositoryImpl(
    private val songDao: SongDao
) : SongRepository {

    override suspend fun getAllSongs(bandId: String): List<Song> =
        songDao.getSongsByBandId(bandId).map { it.toDomain() }

    override fun observeAllSongs(bandId: String): Flow<List<Song>> =
        songDao.observeSongsByBandId(bandId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSong(id: SongId): Song? =
        songDao.getSongById(id.value)?.toDomain()

    override suspend fun addSong(bandId: String, song: Song) {
        songDao.insertSong(song.toEntity(bandId))
    }

    override suspend fun updateSong(bandId: String, song: Song) {
        songDao.updateSong(song.toEntity(bandId))
    }

    override suspend fun deleteSong(bandId: String, id: SongId) {
        songDao.deleteSong(id.value)
    }

    // --- Mappers ---

    private fun Song.toEntity(bandId: String) = SongEntity(
        id = id.value,
        bandId = bandId,
        title = title,
        durationSeconds = durationSeconds,
        key = key,
        tempo = tempo,
        externalLinks = Json.encodeToString(externalLinks)
    )

    private fun SongEntity.toDomain() = Song(
        id = SongId(id),
        title = title,
        durationSeconds = durationSeconds,
        key = key,
        tempo = tempo,
        externalLinks = Json.decodeFromString(externalLinks)
    )
}
