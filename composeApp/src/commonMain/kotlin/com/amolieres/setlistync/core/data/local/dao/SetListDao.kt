package com.amolieres.setlistync.core.data.local.dao

import androidx.room.*
import com.amolieres.setlistync.core.data.local.entity.SetListEntity

@Dao
interface SetListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetList(setList: SetListEntity)

    @Query("SELECT * FROM set_lists WHERE id = :id")
    suspend fun getSetListById(id: String): SetListEntity?

    @Query("SELECT * FROM set_lists")
    suspend fun getAllSetLists(): List<SetListEntity>

    @Update
    suspend fun updateSetList(setList: SetListEntity)

    @Query("DELETE FROM set_lists WHERE id = :id")
    suspend fun deleteSetList(id: String)
}
