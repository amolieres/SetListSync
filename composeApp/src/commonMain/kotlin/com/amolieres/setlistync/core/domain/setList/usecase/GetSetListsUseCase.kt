package com.amolieres.setlistync.core.domain.setList.usecase

import com.amolieres.setlistync.core.domain.setList.model.SetList
import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository

class GetSetListsUseCase(private val repo: SetListRepository) {
    suspend operator fun invoke(bandId: String): List<SetList> = repo.getSetLists(bandId)
}
