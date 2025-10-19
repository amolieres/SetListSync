package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository


class GetBandsForUserUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(userId: String): List<Band> = repo.getBandsForUser(userId)
}