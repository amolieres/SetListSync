package com.amolieres.setlistync.core.data.band

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BandRepositoryLocal : BandRepository {
    private val mutex = Mutex()
    private val storage = mutableMapOf<String, Band>()

    override suspend fun createBand(band: Band) = mutex.withLock {
        storage[band.id] = band
    }

    override suspend fun getBand(bandId: String): Band? = mutex.withLock {
        storage[bandId]
    }

    override suspend fun getAllBands(): List<Band> = mutex.withLock {
        storage.values.toList()
    }

    override suspend fun updateBand(band: Band) = mutex.withLock {
        if (!storage.containsKey(band.id)) throw IllegalArgumentException("Band not found: ${band.id}")
        storage[band.id] = band
    }

    override suspend fun deleteBand(bandId: String): Unit = mutex.withLock {
        storage.remove(bandId)
    }

    override suspend fun deleteAllBands() = mutex.withLock {
        storage.clear()
    }

    override suspend fun addMemberToBand(bandId: String, member: BandMember) = mutex.withLock {
        val band = storage[bandId] ?: throw IllegalArgumentException("Band not found: $bandId")

        storage[bandId] = band.copy(
            members = if (band.members.any { it.userId == member.userId }) {
                // Replace existing or throw — here we replace (merge roles)
                val merged = band.members.map {
                    if (it.userId == member.userId) it.copy(roles = (it.roles + member.roles).distinct(), nickname = member.nickname ?: it.nickname)
                    else it
                }
                merged
            } else {
                band.members + member
            }
        )
    }

    override suspend fun removeMemberFromBand(bandId: String, memberId: String) = mutex.withLock {
        val band = storage[bandId] ?: throw IllegalArgumentException("Band not found: $bandId")
        val member = band.members.find { it.userId == memberId } ?: throw IllegalArgumentException("Member not found: $memberId")
        storage[bandId] = band.copy(members = band.members - member)
    }

    override suspend fun updateMemberInBand(bandId: String, member: BandMember) = mutex.withLock {
        val band = storage[bandId] ?: throw IllegalArgumentException("Band not found: $bandId")
        val existingMember = band.members.find { it.userId == member.userId } ?: throw IllegalArgumentException("Member not found: ${member.userId}")
        storage[bandId] = band.copy(members = band.members - existingMember + member)
    }

    override suspend fun getBandsForUser(userId: String): List<Band> = mutex.withLock {
        storage.values.filter { band -> band.members.any { it.userId == userId } }
    }
}