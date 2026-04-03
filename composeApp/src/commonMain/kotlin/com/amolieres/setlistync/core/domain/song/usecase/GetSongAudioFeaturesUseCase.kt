package com.amolieres.setlistync.core.domain.song.usecase

import com.amolieres.setlistync.core.domain.song.repository.SongCatalogRepository

class GetSongAudioFeaturesUseCase(private val repo: SongCatalogRepository) {
    suspend operator fun invoke(title: String, artist: String): Result<Pair<Int?, String?>> =
        runCatching { repo.getAudioFeatures(title, artist) }
}
