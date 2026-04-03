package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.model.SongSearchResult
import com.amolieres.setlistync.core.domain.song.repository.SongCatalogRepository

class SearchSongsUseCase(private val repo: SongCatalogRepository) {
    suspend operator fun invoke(query: String): Result<List<SongSearchResult>> =
        runCatching {
            repo.searchSongs(query)
        }
}
