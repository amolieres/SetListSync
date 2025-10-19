package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository

class GetAllBandsUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(): List<Band> = repo.getAllBands()
}