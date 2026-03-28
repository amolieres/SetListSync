package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateBandUseCase(private val repo: BandRepository) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        name: String,
        genres: List<String> = emptyList(),
        email: String? = null,
        instagramUrl: String? = null,
        facebookUrl: String? = null,
        tiktokUrl: String? = null
    ): Band {
        val band = Band(
            id = Uuid.random().toString(),
            name = name,
            genres = genres,
            email = email,
            instagramUrl = instagramUrl,
            facebookUrl = facebookUrl,
            tiktokUrl = tiktokUrl
        )
        repo.createBand(band)
        return band
    }
}
