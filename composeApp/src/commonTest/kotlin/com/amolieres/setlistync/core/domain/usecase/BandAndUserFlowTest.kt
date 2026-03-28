package com.amolieres.setlistync.core.domain.usecase

import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import com.amolieres.setlistync.core.domain.band.usecase.AddMemberToBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.CreateBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.GetBandsForUserUseCase
import com.amolieres.setlistync.fake.FakeBandRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BandAndUserFlowTest {

    @Test
    fun `user can be in multiple bands with different roles`() = runTest {
        val bandRepo = FakeBandRepository()
        val createBand = CreateBandUseCase(bandRepo)
        val addMember = AddMemberToBandUseCase(bandRepo)
        val getBandsForUser = GetBandsForUserUseCase(bandRepo)

        val userId = "u1"
        val bandA = createBand(name = "Alpha")
        val bandB = createBand(name = "Beta")

        addMember(bandA.id, BandMember(id = "m1", userId = userId, roles = listOf(Role.GUITAR)))
        addMember(bandB.id, BandMember(id = "m2", userId = userId, roles = listOf(Role.BASS, Role.VOCALS)))

        val bands = getBandsForUser(userId)
        assertEquals(2, bands.size)
        val a = bands.find { it.id == bandA.id }!!
        val b = bands.find { it.id == bandB.id }!!
        assertEquals(1, a.members.size)
        assertEquals(userId, a.members.first().userId)
        assertEquals(2, b.members.first().roles.size)
    }
}
