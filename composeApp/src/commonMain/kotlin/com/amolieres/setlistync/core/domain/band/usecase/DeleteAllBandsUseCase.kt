package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.BandRepository

class DeleteAllBandsUseCase(private val repo: BandRepository) {
    suspend operator fun invoke() = repo.deleteAllBands()
}