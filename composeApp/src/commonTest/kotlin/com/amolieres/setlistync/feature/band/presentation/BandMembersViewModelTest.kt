package com.amolieres.setlistync.feature.band.presentation

import androidx.lifecycle.SavedStateHandle
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.model.BandMember
import com.amolieres.setlistync.core.domain.band.model.Role
import com.amolieres.setlistync.core.domain.band.usecase.AddMemberToBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.RemoveMemberFromBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateMemberInBandUseCase
import com.amolieres.setlistync.fake.FakeBandRepository
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersUiEvent
import com.amolieres.setlistync.feature.band.members.presentation.BandMembersViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BandMembersViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private val fakeRepo = FakeBandRepository()
    private val bandId = "band-1"

    private val viewModel by lazy {
        BandMembersViewModel(
            savedStateHandle = SavedStateHandle(mapOf("bandId" to bandId)),
            observeBand = ObserveBandUseCase(fakeRepo),
            addMember = AddMemberToBandUseCase(fakeRepo),
            removeMember = RemoveMemberFromBandUseCase(fakeRepo),
            updateMember = UpdateMemberInBandUseCase(fakeRepo)
        )
    }

    private val testMembers = listOf(
        BandMember(id = "m-1", userId = null, nickname = "Alice", roles = listOf(Role.VOCALS)),
        BandMember(id = "m-2", userId = null, nickname = "Bob", roles = listOf(Role.GUITAR, Role.BASS))
    )
    private val testBand = Band(id = bandId, name = "Test Band", members = testMembers)

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun kotlinx.coroutines.test.TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }
    }

    // ── State loading ────────────────────────────────────────────────────────

    @Test
    fun `initial state is loading`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `when band emits, members and bandName are updated`() = runTest(testDispatcher) {
        collectUiState()
        fakeRepo.bandFlow.value = testBand
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Test Band", state.bandName)
        assertEquals(testMembers, state.members)
    }

    @Test
    fun `when band emits null, members are empty and bandName is empty`() =
        runTest(testDispatcher) {
            collectUiState()
            fakeRepo.bandFlow.value = null
            advanceUntilIdle()

            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertEquals("", state.bandName)
            assertTrue(state.members.isEmpty())
        }

    @Test
    fun `when band updates members, state reflects new list`() = runTest(testDispatcher) {
        collectUiState()
        fakeRepo.bandFlow.value = testBand
        advanceUntilIdle()

        val newMember = BandMember(id = "m-3", userId = null, nickname = "Charlie")
        fakeRepo.bandFlow.value = testBand.copy(members = testMembers + newMember)
        advanceUntilIdle()

        assertEquals(3, viewModel.uiState.value.members.size)
        assertEquals("Charlie", viewModel.uiState.value.members.last().nickname)
    }

    // ── Member dialog ────────────────────────────────────────────────────────

    @Test
    fun `OnAddMemberClicked shows empty member dialog`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showMemberDialog)
        assertNull(state.editingMember)
        assertEquals("", state.memberNickname)
        assertTrue(state.memberRoles.isEmpty())
    }

    @Test
    fun `OnEditMemberClicked populates dialog with member data`() = runTest(testDispatcher) {
        collectUiState()
        val member = testMembers.first()
        viewModel.onScreenEvent(BandMembersUiEvent.OnEditMemberClicked(member))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.showMemberDialog)
        assertEquals(member, state.editingMember)
        assertEquals(member.nickname, state.memberNickname)
        assertEquals(member.roles.toSet(), state.memberRoles)
    }

    @Test
    fun `OnMemberNicknameChanged updates nickname in state`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberNicknameChanged("Charlie"))
        advanceUntilIdle()

        assertEquals("Charlie", viewModel.uiState.value.memberNickname)
    }

    @Test
    fun `OnMemberRoleToggled adds role when not present`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.KEYS))
        advanceUntilIdle()

        assertTrue(Role.KEYS in viewModel.uiState.value.memberRoles)
    }

    @Test
    fun `OnMemberRoleToggled removes role when already present`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.KEYS))
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.KEYS))
        advanceUntilIdle()

        assertFalse(Role.KEYS in viewModel.uiState.value.memberRoles)
    }

    @Test
    fun `OnMemberRoleToggled can select multiple roles`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.GUITAR))
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.VOCALS))
        advanceUntilIdle()

        val roles = viewModel.uiState.value.memberRoles
        assertEquals(setOf(Role.GUITAR, Role.VOCALS), roles)
    }

    @Test
    fun `OnMemberDialogDismiss hides dialog and resets fields`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberNicknameChanged("Draft"))
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.DRUMS))
        viewModel.onScreenEvent(BandMembersUiEvent.OnMemberDialogDismiss)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.showMemberDialog)
        assertEquals("", state.memberNickname)
        assertTrue(state.memberRoles.isEmpty())
        assertNull(state.editingMember)
    }

    @Test
    fun `OnMemberDialogConfirmed with no editing calls addMember and dismisses dialog`() =
        runTest(testDispatcher) {
            collectUiState()
            viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberNicknameChanged("Charlie"))
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.DRUMS))
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberDialogConfirmed)
            advanceUntilIdle()

            val (calledBandId, addedMember) = checkNotNull(fakeRepo.lastAddedMember)
            assertEquals(bandId, calledBandId)
            assertEquals("Charlie", addedMember.nickname)
            assertEquals(listOf(Role.DRUMS), addedMember.roles)
            assertFalse(viewModel.uiState.value.showMemberDialog)
        }

    @Test
    fun `OnMemberDialogConfirmed with blank nickname saves null nickname`() =
        runTest(testDispatcher) {
            collectUiState()
            viewModel.onScreenEvent(BandMembersUiEvent.OnAddMemberClicked)
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberNicknameChanged("   "))
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberDialogConfirmed)
            advanceUntilIdle()

            val (_, addedMember) = checkNotNull(fakeRepo.lastAddedMember)
            assertNull(addedMember.nickname)
        }

    @Test
    fun `OnMemberDialogConfirmed with editing calls updateMember with correct data`() =
        runTest(testDispatcher) {
            collectUiState()
            val member = testMembers.first()
            viewModel.onScreenEvent(BandMembersUiEvent.OnEditMemberClicked(member))
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberNicknameChanged("Updated"))
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberRoleToggled(Role.BASS))
            viewModel.onScreenEvent(BandMembersUiEvent.OnMemberDialogConfirmed)
            advanceUntilIdle()

            val (calledBandId, updatedMember) = checkNotNull(fakeRepo.lastUpdatedMember)
            assertEquals(bandId, calledBandId)
            assertEquals(member.id, updatedMember.id)
            assertEquals("Updated", updatedMember.nickname)
            assertTrue(Role.BASS in updatedMember.roles)
            assertFalse(viewModel.uiState.value.showMemberDialog)
        }

    @Test
    fun `OnDeleteMemberClicked calls removeMember with correct ids`() =
        runTest(testDispatcher) {
            collectUiState()
            viewModel.onScreenEvent(BandMembersUiEvent.OnDeleteMemberClicked("m-2"))
            advanceUntilIdle()

            val (calledBandId, memberId) = checkNotNull(fakeRepo.lastRemovedMemberId)
            assertEquals(bandId, calledBandId)
            assertEquals("m-2", memberId)
        }
}
