package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.BandPresenter
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import com.amolieres.setlistync.core.domain.band.repository.GigRepository
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveAllBandsUseCase(
    private val bandRepo: BandRepository,
    private val songRepo: SongRepository,
    private val gigRepo: GigRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<BandPresenter>> =
        bandRepo.observeAllBands()
            .flatMapLatest { bands ->
                if (bands.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        bands.map { band ->
                            combine(
                                songRepo.observeAllSongs(band.id),
                                gigRepo.observeGigsByBandId(band.id)
                            ) { songs, gigs ->
                                BandPresenter(
                                    bandId = band.id,
                                    bandName = band.name,
                                    memberCount = band.members.count(),
                                    songCount = songs.size,
                                    gigCount = gigs.size
                                )
                            }
                        }
                    ) { it.toList() }
                }
            }
}
