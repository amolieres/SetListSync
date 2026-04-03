package com.amolieres.setlistync.core.domain.song.repository

import com.amolieres.setlistync.core.domain.song.model.SongSearchResult

interface SongCatalogRepository {
    suspend fun searchSongs(query: String): List<SongSearchResult>
    suspend fun getAudioFeatures(title: String, artist: String): Pair<Int?, String?> // bpm, key
}
