package com.amolieres.setlistync.core.data.local.dao

import androidx.room.*
import com.amolieres.setlistync.core.data.local.entity.BandEntity

@Dao
interface BandDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBand(band: BandEntity)

    @Query("SELECT * FROM bands WHERE id = :id")
    suspend fun getBandById(id: String): BandEntity?

    @Query("SELECT * FROM bands")
    suspend fun getAllBands(): List<BandEntity>

    @Update
    suspend fun updateBand(band: BandEntity)

    @Query("DELETE FROM bands WHERE id = :id")
    suspend fun deleteBand(id: String)
}
