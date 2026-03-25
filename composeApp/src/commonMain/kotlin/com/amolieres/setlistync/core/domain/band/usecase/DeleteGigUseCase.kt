package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.repository.GigRepository

class DeleteGigUseCase(private val repo: GigRepository) {
    suspend operator fun invoke(gigId: String) = repo.deleteGig(gigId)
}
