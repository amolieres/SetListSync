package com.amolieres.setlistync.core.data.setlist

import com.amolieres.setlistync.core.data.local.dao.SetListDao
import com.amolieres.setlistync.core.data.local.entity.SetListEntity
import com.amolieres.setlistync.core.domain.setList.model.SetList
import com.amolieres.setlistync.core.domain.setList.repository.SetListRepository
import com.amolieres.setlistync.core.domain.song.model.SongId
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SetListRepositoryImpl(
    private val setListDao: SetListDao
) : SetListRepository {

    override suspend fun getSetLists(bandId: String): List<SetList> =
        setListDao.getSetListsByBandId(bandId).map { it.toDomain() }

    override suspend fun addSetList(bandId: String, setList: SetList) {
        setListDao.insertSetList(setList.toEntity(bandId))
    }

    override suspend fun getSetList(id: String): SetList? =
        setListDao.getSetListById(id)?.toDomain()

    override suspend fun updateSetList(bandId: String, setList: SetList) {
        setListDao.updateSetList(setList.toEntity(bandId))
    }

    override suspend fun deleteSetList(id: String) {
        setListDao.deleteSetList(id)
    }

    // --- Mappers ---

    private fun SetList.toEntity(bandId: String) = SetListEntity(
        id = id,
        bandId = bandId,
        name = name,
        orderedSongIds = Json.encodeToString(orderedSongIds.map { it.value })
    )

    private fun SetListEntity.toDomain() = SetList(
        id = id,
        name = name,
        orderedSongIds = Json.decodeFromString<List<String>>(orderedSongIds).map { SongId(it) }
    )
}
