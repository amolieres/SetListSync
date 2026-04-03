package com.amolieres.setlistync.core.data.remote.getsongbpm

import com.amolieres.setlistync.core.data.remote.getsongbpm.dto.GetSongBpmSearchResponse
import com.amolieres.setlistync.core.data.remote.getsongbpm.dto.GetSongBpmTrackDto
import com.amolieres.setlistync.core.network.ApiConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class GetSongBpmApi(private val client: HttpClient) {

    suspend fun search(title: String, artist: String): GetSongBpmTrackDto? =
        client.get("https://api.getsongbpm.com/search/") {
            parameter("api_key", ApiConfig.GET_SONG_BPM_API_KEY)
            parameter("type", "both")
            parameter("lookup", "$title $artist")
        }.body<GetSongBpmSearchResponse>().search?.firstOrNull()
}
