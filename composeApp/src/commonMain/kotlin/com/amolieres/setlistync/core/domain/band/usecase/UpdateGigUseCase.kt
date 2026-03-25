package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.Gig
import com.amolieres.setlistync.core.domain.band.repository.GigRepository

class UpdateGigUseCase(private val repo: GigRepository) {
    suspend operator fun invoke(gig: Gig) = repo.updateGig(gig)
}
