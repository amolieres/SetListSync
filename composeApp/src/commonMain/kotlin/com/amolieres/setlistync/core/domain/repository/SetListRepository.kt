package com.amolieres.setlistync.core.domain.repository

import com.amolieres.setlistync.core.domain.model.SetList


interface SetListRepository {
    suspend fun getSetLists(bandId: String): List<SetList>
    suspend fun addSetList(bandId: String, setList: SetList)
    suspend fun updateSetList(bandId: String, setList: SetList)
}