package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import kotlinx.coroutines.flow.Flow

class ObserveBandUseCase(private val repo: BandRepository) {
    operator fun invoke(bandId: String): Flow<Band?> = repo.observeBand(bandId)
}
