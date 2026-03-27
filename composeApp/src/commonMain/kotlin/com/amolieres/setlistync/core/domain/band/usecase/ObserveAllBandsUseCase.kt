package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import kotlinx.coroutines.flow.Flow

class ObserveAllBandsUseCase(private val repo: BandRepository) {
    operator fun invoke(): Flow<List<Band>> = repo.observeAllBands()
}
