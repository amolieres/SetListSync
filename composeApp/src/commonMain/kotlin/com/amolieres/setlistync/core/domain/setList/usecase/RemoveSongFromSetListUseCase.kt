package com.amolieres.setlistync.core.domain.setList.usecase

import com.amolieres.setlistync.core.domain.setList.model.SetList
import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository
import com.amolieres.setlistync.core.domain.song.model.SongId

class RemoveSongFromSetListUseCase(private val repo: SetListRepository) {
    suspend operator fun invoke(
        bandId: String,
        setListId: String,
        songId: SongId
    ): Result<SetList> = runCatching {
        val setList = repo.getSetList(setListId)
            ?: throw IllegalArgumentException("SetList not found: $setListId")
        val updated = setList.copy(orderedSongIds = setList.orderedSongIds.filter { it != songId })
        repo.updateSetList(bandId, updated)
        updated
    }
}
