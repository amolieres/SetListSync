package com.amolieres.setlistync.app.navigation

object Destinations {
    const val UserAuth = "user_auth"
    const val MainScreen = "main_screen"
    const val Settings = "settings"
    const val BandDetail = "band_detail/{bandId}"
    fun bandDetail(bandId: String) = "band_detail/$bandId"
    const val BandMembers = "band_members/{bandId}"
    fun bandMembers(bandId: String) = "band_members/$bandId"
    const val BandSongs = "band_songs/{bandId}"
    fun bandSongs(bandId: String) = "band_songs/$bandId"
    const val BandCreation = "band_creation"
    const val BandEdit = "band_edit/{bandId}"
    fun bandEdit(bandId: String) = "band_edit/$bandId"
    const val BandGigs = "band_gigs/{bandId}"
    fun bandGigs(bandId: String) = "band_gigs/$bandId"
    const val BandSongDetail = "band_songs/{bandId}/song?songId={songId}"
    fun newSong(bandId: String) = "band_songs/$bandId/song"
    fun editSong(bandId: String, songId: String) = "band_songs/$bandId/song?songId=$songId"
    const val GigDetail = "band_gigs/{bandId}/gig?gigId={gigId}"
    fun newGig(bandId: String) = "band_gigs/$bandId/gig"
    fun editGig(bandId: String, gigId: String) = "band_gigs/$bandId/gig?gigId=$gigId"
}
