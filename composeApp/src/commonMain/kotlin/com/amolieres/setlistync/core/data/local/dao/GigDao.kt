package com.amolieres.setlistync.core.data.local.dao

import androidx.room.*
import com.amolieres.setlistync.core.data.local.entity.GigEntity

@Dao
interface GigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGig(gig: GigEntity)

    @Query("SELECT * FROM gigs WHERE id = :id")
    suspend fun getGigById(id: String): GigEntity?

    @Query("SELECT * FROM gigs WHERE bandId = :bandId")
    suspend fun getGigsByBandId(bandId: String): List<GigEntity>

    @Query("SELECT * FROM gigs")
    suspend fun getAllGigs(): List<GigEntity>

    @Update
    suspend fun updateGig(gig: GigEntity)

    @Query("DELETE FROM gigs WHERE id = :id")
    suspend fun deleteGig(id: String)

    @Query("DELETE FROM gigs WHERE bandId = :bandId")
    suspend fun deleteGigsByBandId(bandId: String)
}
