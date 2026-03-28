package com.amolieres.setlistync.app.navigation

object Destinations {
    const val UserAuth = "user_auth"
    const val MainScreen = "main_screen"
    const val BandDetail = "band_detail/{bandId}"
    fun bandDetail(bandId: String) = "band_detail/$bandId"
    const val BandMembers = "band_members/{bandId}"
    fun bandMembers(bandId: String) = "band_members/$bandId"
}