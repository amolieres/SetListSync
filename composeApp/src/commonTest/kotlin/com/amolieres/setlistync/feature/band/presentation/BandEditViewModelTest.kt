package com.amolieres.setlistync.feature.band.presentation

import androidx.lifecycle.SavedStateHandle
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.usecase.GetBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.UpdateBandUseCase
import com.amolieres.setlistync.fake.FakeBandRepository
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditEvent
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditUiEvent
import com.amolieres.setlistync.feature.band.edit.presentation.BandEditViewModel
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
class BandEditViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private val fakeRepo = FakeBandRepository()
    private val bandId = "band-1"

    private val testBand = Band(
        id = bandId,
        name = "Test Band",
        email = "band@test.com",
        instagramUrl = "https://instagram.com/test",
        facebookUrl = null,
        tiktokUrl = null,
        genres = listOf("Rock", "Indie")
    )

    private fun buildViewModel() = BandEditViewModel(
        savedStateHandle = SavedStateHandle(mapOf("bandId" to bandId)),
        getBand = GetBandUseCase(fakeRepo),
        updateBand = UpdateBandUseCase(fakeRepo)
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun kotlinx.coroutines.test.TestScope.collectEvents(
        vm: BandEditViewModel,
        sink: MutableList<BandEditEvent>
    ) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.events.collect { sink.add(it) }
        }
    }

    // ── Loading ──────────────────────────────────────────────────────────────

    @Test
    fun `initial state is loading`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        assertTrue(vm.uiState.value.isLoading)
    }

    @Test
    fun `after init band fields are pre-populated`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertFalse(state.bandNotFound)
        assertEquals("Test Band", state.name)
        assertEquals("band@test.com", state.email)
        assertEquals("https://instagram.com/test", state.instagramUrl)
        assertEquals("", state.facebookUrl)
        assertEquals("", state.tiktokUrl)
        assertEquals(listOf("Rock", "Indie"), state.genres)
    }

    @Test
    fun `when band not found, bandNotFound is true`() = runTest(testDispatcher) {
        // no seedBand → getBand returns null
        val vm = buildViewModel()
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.bandNotFound)
    }

    // ── Field changes ────────────────────────────────────────────────────────

    @Test
    fun `OnNameChanged updates name`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnNameChanged("New Name"))
        advanceUntilIdle()

        assertEquals("New Name", vm.uiState.value.name)
    }

    @Test
    fun `OnEmailChanged updates email`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnEmailChanged("new@mail.com"))
        advanceUntilIdle()

        assertEquals("new@mail.com", vm.uiState.value.email)
    }

    @Test
    fun `OnInstagramChanged updates instagramUrl`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnInstagramChanged("https://instagram.com/new"))
        advanceUntilIdle()

        assertEquals("https://instagram.com/new", vm.uiState.value.instagramUrl)
    }

    @Test
    fun `OnGenreInputChanged updates genreInput`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnGenreInputChanged("Jazz"))
        advanceUntilIdle()

        assertEquals("Jazz", vm.uiState.value.genreInput)
    }

    // ── Genre management ─────────────────────────────────────────────────────

    @Test
    fun `OnAddGenreClicked adds genre and clears input`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnGenreInputChanged("Jazz"))
        vm.onScreenEvent(BandEditUiEvent.OnAddGenreClicked)
        advanceUntilIdle()

        assertTrue("Jazz" in vm.uiState.value.genres)
        assertEquals("", vm.uiState.value.genreInput)
    }

    @Test
    fun `OnAddGenreClicked with blank input does nothing`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        val before = vm.uiState.value.genres.size
        vm.onScreenEvent(BandEditUiEvent.OnGenreInputChanged("   "))
        vm.onScreenEvent(BandEditUiEvent.OnAddGenreClicked)
        advanceUntilIdle()

        assertEquals(before, vm.uiState.value.genres.size)
    }

    @Test
    fun `OnRemoveGenre removes genre from list`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnRemoveGenre("Rock"))
        advanceUntilIdle()

        assertFalse("Rock" in vm.uiState.value.genres)
        assertTrue("Indie" in vm.uiState.value.genres)
    }

    // ── Save ─────────────────────────────────────────────────────────────────

    @Test
    fun `OnSaveClicked calls updateBand with correct data and emits NavigateBack`() =
        runTest(testDispatcher) {
            fakeRepo.seedBand(testBand)
            val vm = buildViewModel()
            advanceUntilIdle()

            val events = mutableListOf<BandEditEvent>()
            collectEvents(vm, events)

            vm.onScreenEvent(BandEditUiEvent.OnNameChanged("Updated Band"))
            vm.onScreenEvent(BandEditUiEvent.OnEmailChanged("updated@mail.com"))
            vm.onScreenEvent(BandEditUiEvent.OnFacebookChanged("https://facebook.com/updated"))
            vm.onScreenEvent(BandEditUiEvent.OnTiktokChanged(""))
            vm.onScreenEvent(BandEditUiEvent.OnRemoveGenre("Indie"))
            vm.onScreenEvent(BandEditUiEvent.OnSaveClicked)
            advanceUntilIdle()

            val saved = checkNotNull(fakeRepo.lastUpdatedBand)
            assertEquals("Updated Band", saved.name)
            assertEquals("updated@mail.com", saved.email)
            assertEquals("https://instagram.com/test", saved.instagramUrl) // unchanged
            assertEquals("https://facebook.com/updated", saved.facebookUrl)
            assertNull(saved.tiktokUrl)
            assertEquals(listOf("Rock"), saved.genres)

            assertEquals(1, events.size)
            assertEquals(BandEditEvent.NavigateBack, events.first())

            assertFalse(vm.uiState.value.isSaving)
        }

    @Test
    fun `OnSaveClicked with blank name does nothing`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnNameChanged("   "))
        vm.onScreenEvent(BandEditUiEvent.OnSaveClicked)
        advanceUntilIdle()

        assertNull(fakeRepo.lastUpdatedBand)
    }

    @Test
    fun `OnSaveClicked with blank email saves null email`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        vm.onScreenEvent(BandEditUiEvent.OnEmailChanged("   "))
        vm.onScreenEvent(BandEditUiEvent.OnSaveClicked)
        advanceUntilIdle()

        assertNull(fakeRepo.lastUpdatedBand?.email)
    }

    // ── Back navigation ──────────────────────────────────────────────────────

    @Test
    fun `OnBackClicked emits NavigateBack without saving`() = runTest(testDispatcher) {
        fakeRepo.seedBand(testBand)
        val vm = buildViewModel()
        advanceUntilIdle()

        val events = mutableListOf<BandEditEvent>()
        collectEvents(vm, events)

        vm.onScreenEvent(BandEditUiEvent.OnNameChanged("Changed"))
        vm.onScreenEvent(BandEditUiEvent.OnBackClicked)
        advanceUntilIdle()

        assertNull(fakeRepo.lastUpdatedBand)
        assertEquals(1, events.size)
        assertEquals(BandEditEvent.NavigateBack, events.first())
    }
}
