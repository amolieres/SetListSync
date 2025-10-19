package com.amolieres.setlistync.core.domain.setList.repository

import com.amolieres.setlistync.core.domain.setList.model.SetList

interface SetListRepository {
    suspend fun getSetLists(bandId: String): List<SetList>
    suspend fun addSetList(bandId: String, setList: SetList)
    suspend fun updateSetList(bandId: String, setList: SetList)
}