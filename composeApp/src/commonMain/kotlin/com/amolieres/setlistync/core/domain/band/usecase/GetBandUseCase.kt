package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository

class GetBandUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(bandId: String): Band? = repo.getBand(bandId)
}