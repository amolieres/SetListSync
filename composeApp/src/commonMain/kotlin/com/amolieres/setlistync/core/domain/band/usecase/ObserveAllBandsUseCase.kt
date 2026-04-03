package com.amolieres.setlistync.core.domain.band.usecase

import com.amolieres.setlistync.core.domain.band.model.BandPresenter
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import com.amolieres.setlistync.core.domain.song.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ObserveAllBandsUseCase(
    private val bandRepo: BandRepository,
    private val songRepo: SongRepository
) {
    operator fun invoke(): Flow<List<BandPresenter>> =
        bandRepo.observeAllBands()
            .flatMapLatest { bands ->
                if (bands.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    combine(
                        bands.map { band ->
                            songRepo.observeAllSongs(band.id)
                                .map { songs -> BandPresenter.fromBandWithSongCount(band, songs.size) }
                        }
                    ) { it.toList() }
                }
            }
}
