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
    const val BandSongDetail = "band_songs/{bandId}/song?songId={songId}"
    fun newSong(bandId: String) = "band_songs/$bandId/song"
    fun editSong(bandId: String, songId: String) = "band_songs/$bandId/song?songId=$songId"
    // Gig edit/create form (gigId nullable — null = create mode)
    const val GigEdit = "band_gigs/{bandId}/gig_edit?gigId={gigId}"
    fun newGig(bandId: String) = "band_gigs/$bandId/gig_edit"
    fun editGigInfo(bandId: String, gigId: String) = "band_gigs/$bandId/gig_edit?gigId=$gigId"
    // Gig detail / setlist manager (gigId required path param, isEditing optional query param)
    const val GigDetail = "band_gigs/{bandId}/gig/{gigId}?isEditing={isEditing}"
    fun gigDetail(bandId: String, gigId: String, isEditing: Boolean = false) =
        "band_gigs/$bandId/gig/$gigId?isEditing=$isEditing"
}
