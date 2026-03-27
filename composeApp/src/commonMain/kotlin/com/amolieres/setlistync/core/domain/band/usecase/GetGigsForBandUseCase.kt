package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository

class GetGigsForBandUseCase(private val repo: GigRepository) {
    suspend operator fun invoke(bandId: String): List<Gig> = repo.getGigsByBandId(bandId)
}
