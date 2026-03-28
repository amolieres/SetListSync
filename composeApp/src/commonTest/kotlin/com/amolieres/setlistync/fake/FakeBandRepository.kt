package com.amolieres.setlistync.fake

import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.repository.BandRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory implementation of [BandRepository] for tests.
 *
 * - [bandFlow] can be set directly to control what [observeBand] emits (ViewModel tests).
 * - Real in-memory storage (createBand / addMemberToBand / …) supports integration-style tests.
 * - Recorded calls (lastAddedMember, …) allow asserting side-effects in ViewModel tests.
 */
class FakeBandRepository : BandRepository {

    private val bands = mutableMapOf<String, Band>()

    /** Directly controllable flow for [observeBand] — set this in ViewModel tests. */
    val bandFlow = MutableStateFlow<Band?>(null)

    // Recorded calls for assertions in ViewModel tests
    var lastAddedMember: Pair<String, BandMember>? = null
    var lastUpdatedMember: Pair<String, BandMember>? = null
    var lastRemovedMemberId: Pair<String, String>? = null
    var deletedBandId: String? = null

    // ── Observation ──────────────────────────────────────────────────────────

    override fun observeBand(bandId: String): Flow<Band?> = bandFlow
    override fun observeAllBands(): Flow<List<Band>> = MutableStateFlow(bands.values.toList())

    // ── Band CRUD ────────────────────────────────────────────────────────────

    override suspend fun createBand(band: Band) {
        bands[band.id] = band
    }

    override suspend fun getBand(bandId: String): Band? = bands[bandId]

    override suspend fun getAllBands(): List<Band> = bands.values.toList()

    override suspend fun updateBand(band: Band) {
        bands[band.id] = band
    }

    override suspend fun deleteBand(bandId: String) {
        deletedBandId = bandId
        bands.remove(bandId)
    }

    override suspend fun deleteAllBands() {
        bands.clear()
    }

    override suspend fun getBandsForUser(userId: String): List<Band> =
        bands.values.filter { band -> band.members.any { it.userId == userId } }

    // ── Member operations ────────────────────────────────────────────────────

    override suspend fun addMemberToBand(bandId: String, member: BandMember) {
        lastAddedMember = bandId to member
        val band = bands[bandId] ?: return
        bands[bandId] = band.copy(members = band.members + member)
    }

    override suspend fun removeMemberFromBand(bandId: String, memberId: String) {
        lastRemovedMemberId = bandId to memberId
        val band = bands[bandId] ?: return
        bands[bandId] = band.copy(members = band.members.filter { it.id != memberId })
    }

    override suspend fun updateMemberInBand(bandId: String, member: BandMember) {
        lastUpdatedMember = bandId to member
        val band = bands[bandId] ?: return
        bands[bandId] = band.copy(members = band.members.map { if (it.id == member.id) member else it })
    }
}
