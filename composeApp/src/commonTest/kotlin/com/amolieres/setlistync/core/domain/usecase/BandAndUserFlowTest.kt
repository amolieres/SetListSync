package com.amolieres.setlistync.core.domain.usecase

import com.amolieres.setlistync.core.domain.band.model.Band
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
        val bandA = Band(id = "b1", name = "Alpha")
        val bandB = Band(id = "b2", name = "Beta")

        createBand(bandA)
        createBand(bandB)

        addMember("b1", BandMember(id = "m1", userId = userId, roles = listOf(Role.GUITAR)))
        addMember("b2", BandMember(id = "m2", userId = userId, roles = listOf(Role.BASS, Role.VOCALS)))

        val bands = getBandsForUser(userId)
        assertEquals(2, bands.size)
        val a = bands.find { it.id == "b1" }!!
        val b = bands.find { it.id == "b2" }!!
        assertEquals(1, a.members.size)
        assertEquals(userId, a.members.first().userId)
        assertEquals(2, b.members.first().roles.size)
    }
}
