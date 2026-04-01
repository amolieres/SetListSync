package com.amolieres.setlistync.fake

import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory implementation of [SongRepository] for tests.
 *
 * - [songsFlow] can be set directly to control what [observeAllSongs] emits (ViewModel tests).
 * - [deletedSongs] records every (bandId, songId) pair passed to [deleteSong] for assertions.
 */
class FakeSongRepository : SongRepository {

    /** Directly controllable flow for [observeAllSongs] — set this in ViewModel tests. */
    val songsFlow = MutableStateFlow<List<Song>>(emptyList())

    /** Records every (bandId, song) passed to addSong for assertions. */
    val addedSongs = mutableListOf<Pair<String, Song>>()

    /** Records every (bandId, song) passed to updateSong for assertions. */
    val updatedSongs = mutableListOf<Pair<String, Song>>()

    /** Records every (bandId, songId) passed to deleteSong for assertions. */
    val deletedSongs = mutableListOf<Pair<String, SongId>>()

    // ── Observation ──────────────────────────────────────────────────────────

    override fun observeAllSongs(bandId: String): Flow<List<Song>> = songsFlow

    // ── Song CRUD ────────────────────────────────────────────────────────────

    override suspend fun getAllSongs(bandId: String): List<Song> = songsFlow.value

    override suspend fun getSong(id: SongId): Song? =
        songsFlow.value.firstOrNull { it.id == id }

    override suspend fun addSong(bandId: String, song: Song) {
        addedSongs.add(bandId to song)
        songsFlow.value = songsFlow.value + song
    }

    override suspend fun updateSong(bandId: String, song: Song) {
        updatedSongs.add(bandId to song)
        songsFlow.value = songsFlow.value.map { if (it.id == song.id) song else it }
    }

    override suspend fun deleteSong(bandId: String, id: SongId) {
        deletedSongs.add(bandId to id)
        songsFlow.value = songsFlow.value.filter { it.id != id }
    }
}
