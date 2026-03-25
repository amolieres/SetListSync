package com.amolieres.setlistync.core.domain.setList.usecase

import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository

class DeleteSetListUseCase(private val repo: SetListRepository) {
    suspend operator fun invoke(setListId: String) = repo.deleteSetList(setListId)
}
