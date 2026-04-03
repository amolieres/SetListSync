package com.amolieres.setlistync.core.data.remote.deezer

import com.amolieres.setlistync.core.data.remote.deezer.dto.DeezerSearchResponse
import com.amolieres.setlistync.core.data.remote.deezer.dto.DeezerTrackDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class DeezerApi(private val client: HttpClient) {

    suspend fun search(query: String): List<DeezerTrackDto> =
        client.get("https://api.deezer.com/search") {
            parameter("q", query)
            parameter("limit", 10)
        }.body<DeezerSearchResponse>().data
}
