package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.BandRepository

class DeleteBandUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(bandId: String) = repo.deleteBand(bandId)
}