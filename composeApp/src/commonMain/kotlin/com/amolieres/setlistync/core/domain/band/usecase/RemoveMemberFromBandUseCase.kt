package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.BandRepository


class RemoveMemberFromBandUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(bandId: String, userId: String) = repo.removeMemberFromBand(bandId, userId)
}