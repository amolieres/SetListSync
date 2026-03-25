package com.amolieres.setlistync.core.domain.setList.usecase

import com.amolieres.setlistync.core.domain.setList.model.SetList
import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository
import com.amolieres.setlistync.core.domain.song.model.SongId

class ReorderSetListUseCase(private val repo: SetListRepository) {
    suspend operator fun invoke(
        bandId: String,
        setList: SetList,
        orderedSongIds: List<SongId>
    ): Result<SetList> = runCatching {
        val updated = setList.copy(orderedSongIds = orderedSongIds)
        repo.updateSetList(bandId, updated)
        updated
    }
}
