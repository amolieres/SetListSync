package com.amolieres.setlistync.core.data.local.dao

import androidx.room.*
import com.amolieres.setlistync.core.data.local.entity.BandMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BandMemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: BandMemberEntity)

    @Query("SELECT * FROM band_members WHERE id = :id")
    suspend fun getMemberById(id: String): BandMemberEntity?

    @Query("SELECT * FROM band_members WHERE bandId = :bandId")
    suspend fun getMembersByBandId(bandId: String): List<BandMemberEntity>

    @Query("SELECT * FROM band_members WHERE bandId = :bandId")
    fun observeMembersByBandId(bandId: String): Flow<List<BandMemberEntity>>

    @Query("SELECT * FROM band_members")
    fun observeAllMembers(): Flow<List<BandMemberEntity>>

    @Update
    suspend fun updateMember(member: BandMemberEntity)

    @Query("DELETE FROM band_members WHERE id = :id")
    suspend fun deleteMember(id: String)

    @Query("DELETE FROM band_members WHERE bandId = :bandId")
    suspend fun deleteMembersByBandId(bandId: String)

    @Query("SELECT DISTINCT bandId FROM band_members WHERE userId = :userId")
    suspend fun getBandIdsByUserId(userId: String): List<String>
}
