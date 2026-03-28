package com.amolieres.setlistync.core.data.band

import com.amolieres.setlistync.core.data.local.dao.BandDao
import com.amolieres.setlistync.core.data.local.dao.BandMemberDao
import com.amolieres.setlistync.core.data.local.entity.BandEntity
import com.amolieres.setlistync.core.data.local.entity.BandMemberEntity
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class BandRepositoryImpl(
    private val bandDao: BandDao,
    private val bandMemberDao: BandMemberDao
) : BandRepository {

    override suspend fun createBand(band: Band) {
        bandDao.insertBand(band.toEntity())
        band.members.forEach { member ->
            bandMemberDao.insertMember(member.toEntity(band.id))
        }
    }

    override suspend fun getBand(bandId: String): Band? {
        val entity = bandDao.getBandById(bandId) ?: return null
        val members = bandMemberDao.getMembersByBandId(bandId).map { it.toDomain() }
        return entity.toDomain(members)
    }

    override suspend fun getAllBands(): List<Band> {
        return bandDao.getAllBands().map { entity ->
            val members = bandMemberDao.getMembersByBandId(entity.id).map { it.toDomain() }
            entity.toDomain(members)
        }
    }

    // Reactive — reacts to changes in both tables so member count stays live
    override fun observeAllBands(): Flow<List<Band>> =
        combine(
            bandDao.observeAllBands(),
            bandMemberDao.observeAllMembers()
        ) { bands, allMembers ->
            bands.map { entity ->
                val members = allMembers.filter { it.bandId == entity.id }.map { it.toDomain() }
                entity.toDomain(members)
            }
        }

    override fun observeBand(bandId: String): Flow<Band?> =
        combine(
            bandDao.observeBandById(bandId),
            bandMemberDao.observeMembersByBandId(bandId)
        ) { entity, members ->
            entity?.toDomain(members.map { it.toDomain() })
        }

    override suspend fun updateBand(band: Band) {
        bandDao.updateBand(band.toEntity())
    }

    override suspend fun deleteBand(bandId: String) {
        bandMemberDao.deleteMembersByBandId(bandId)
        bandDao.deleteBand(bandId)
    }

    override suspend fun deleteAllBands() {
        bandDao.getAllBands().forEach { entity ->
            bandMemberDao.deleteMembersByBandId(entity.id)
        }
        bandDao.getAllBands().forEach { bandDao.deleteBand(it.id) }
    }

    override suspend fun addMemberToBand(bandId: String, member: BandMember) {
        val existing = bandMemberDao.getMemberById(member.id)
        if (existing != null) {
            val mergedRoles = (Json.decodeFromString<List<Role>>(existing.roles) + member.roles).distinct()
            bandMemberDao.updateMember(
                existing.copy(
                    roles = Json.encodeToString(mergedRoles),
                    nickname = member.nickname ?: existing.nickname
                )
            )
        } else {
            bandMemberDao.insertMember(member.toEntity(bandId))
        }
    }

    override suspend fun removeMemberFromBand(bandId: String, memberId: String) {
        bandMemberDao.deleteMember(memberId)
    }

    override suspend fun updateMemberInBand(bandId: String, member: BandMember) {
        bandMemberDao.updateMember(member.toEntity(bandId))
    }

    override suspend fun getBandsForUser(userId: String): List<Band> {
        val bandIds = bandMemberDao.getBandIdsByUserId(userId)
        return bandIds.mapNotNull { getBand(it) }
    }

    // --- Mappers ---

    private fun Band.toEntity() = BandEntity(
        id = id,
        name = name,
        email = email,
        instagramUrl = instagramUrl,
        facebookUrl = facebookUrl,
        tiktokUrl = tiktokUrl,
        genres = Json.encodeToString(genres)
    )

    private fun BandMember.toEntity(bandId: String) = BandMemberEntity(
        id = id,
        bandId = bandId,
        userId = userId,
        nickname = nickname,
        roles = Json.encodeToString(roles)
    )

    private fun BandEntity.toDomain(members: List<BandMember>) = Band(
        id = id,
        name = name,
        members = members,
        email = email,
        instagramUrl = instagramUrl,
        facebookUrl = facebookUrl,
        tiktokUrl = tiktokUrl,
        genres = Json.decodeFromString(genres)
    )

    private fun BandMemberEntity.toDomain() = BandMember(
        id = id,
        userId = userId,
        nickname = nickname,
        roles = Json.decodeFromString(roles)
    )
}
