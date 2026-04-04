package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import kotlinx.coroutines.flow.Flow

class ObserveGigsForBandUseCase(private val repo: GigRepository) {
    operator fun invoke(bandId: String): Flow<List<Gig>> = repo.observeGigsByBandId(bandId)
}
