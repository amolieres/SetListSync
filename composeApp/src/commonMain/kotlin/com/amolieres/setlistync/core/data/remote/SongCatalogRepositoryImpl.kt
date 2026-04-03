package com.amolieres.setlistync.core.data.remote

import com.amolieres.setlistync.core.data.remote.deezer.DeezerApi
import com.amolieres.setlistync.core.data.remote.getsongbpm.GetSongBpmApi
import com.amolieres.setlistync.core.domain.song.model.SongSearchResult
import com.amolieres.setlistync.core.domain.song.repository.SongCatalogRepository

class SongCatalogRepositoryImpl(
    private val deezerApi: DeezerApi,
    private val getSongBpmApi: GetSongBpmApi
) : SongCatalogRepository {

    override suspend fun searchSongs(query: String): List<SongSearchResult> {
        println("[Deezer] searchSongs — query=\"$query\"")
        return try {
            val results = deezerApi.search(query).map { track ->
                SongSearchResult(
                    title = track.title,
                    artist = track.artist.name,
                    durationSeconds = track.duration
                )
            }
            println("[Deezer] searchSongs — ${results.size} result(s) for \"$query\"")
            results
        } catch (e: Exception) {
            println("[Deezer] searchSongs — ERROR for \"$query\": ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }

    override suspend fun getAudioFeatures(title: String, artist: String): Pair<Int?, String?> {
        println("[GetSongBPM] getAudioFeatures — title=\"$title\" artist=\"$artist\"")
        return try {
            val result = getSongBpmApi.search(title, artist)
            val bpm = result?.tempo?.toIntOrNull()
            val key = result?.keyOf
            println("[GetSongBPM] getAudioFeatures — bpm=$bpm key=$key for \"$title\" by \"$artist\"")
            Pair(bpm, key)
        } catch (e: Exception) {
            println("[GetSongBPM] getAudioFeatures — ERROR for \"$title\" by \"$artist\": ${e::class.simpleName}: ${e.message}")
            throw e
        }
    }
}
