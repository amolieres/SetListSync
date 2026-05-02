package com.amolieres.setlistync.fake

import com.amolieres.setlistync.core.domain.song.model.SongSearchResult
import com.amolieres.setlistync.core.domain.song.repository.SongCatalogRepository

class FakeSongCatalogRepository : SongCatalogRepository {
    override suspend fun searchSongs(query: String): List<SongSearchResult> = emptyList()
    override suspend fun getAudioFeatures(title: String, artist: String): Pair<Int?, String?> = null to null
}
