package com.amolieres.setlistync.core.domain.band.model

data class BandPresenter(
    val bandId: String,
    val bandName: String,
    val memberCount: Int,
    val songCount: Int,
    val setListCount: Int = 0 //TODO: Use real data
) {
    companion object {
        fun fromBandWithSongCount(band: Band, songCount: Int): BandPresenter {
            return BandPresenter(
                bandId = band.id,
                bandName = band.name,
                memberCount = band.members.count(),
                songCount = songCount
            )
        }
    }
}
