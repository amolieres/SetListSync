package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository

class UpdateBandUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(band: Band) = repo.updateBand(band)
}
