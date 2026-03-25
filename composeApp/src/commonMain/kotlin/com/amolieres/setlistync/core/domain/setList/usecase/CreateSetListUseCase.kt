package com.amolieres.setlistync.core.domain.setList.usecase

import com.amolieres.setlistync.core.domain.setList.model.SetList
import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CreateSetListUseCase(private val repo: SetListRepository) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(bandId: String, name: String): Result<SetList> = runCatching {
        val setList = SetList(
            id = Uuid.random().toString(),
            name = name.trim()
        )
        repo.addSetList(bandId, setList)
        setList
    }
}
