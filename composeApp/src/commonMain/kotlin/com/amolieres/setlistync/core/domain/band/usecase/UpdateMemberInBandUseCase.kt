package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.repository.BandRepository

class UpdateMemberInBandUseCase(private val repo: BandRepository) {
    suspend operator fun invoke(bandId: String, member: BandMember) = repo.updateMemberInBand(bandId, member)
}