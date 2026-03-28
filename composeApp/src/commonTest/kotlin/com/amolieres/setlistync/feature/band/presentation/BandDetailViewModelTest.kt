package com.amolieres.setlistync.feature.band.presentation

import androidx.lifecycle.SavedStateHandle
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.usecase.DeleteBandUseCase
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.fake.FakeBandRepository
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailEvent
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailUiEvent
import com.amolieres.setlistync.feature.band.detail.presentation.BandDetailViewModel
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
class BandDetailViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private val fakeRepo = FakeBandRepository()
    private val bandId = "band-1"

    private val viewModel by lazy {
        BandDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("bandId" to bandId)),
            observeBand = ObserveBandUseCase(fakeRepo),
            deleteBand = DeleteBandUseCase(fakeRepo)
        )
    }

    private val testBand = Band(id = bandId, name = "Test Band")

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

    private fun kotlinx.coroutines.test.TestScope.collectEvents(
        sink: MutableList<BandDetailEvent>
    ) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.event.collect { sink.add(it) }
        }
    }

    // ── State loading ────────────────────────────────────────────────────────

    @Test
    fun `initial state is loading`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `when band emits, state has band and isLoading false`() = runTest(testDispatcher) {
        collectUiState()
        fakeRepo.bandFlow.value = testBand
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(testBand, state.band)
    }

    @Test
    fun `when band emits null, state has null band and isLoading false`() = runTest(testDispatcher) {
        collectUiState()
        fakeRepo.bandFlow.value = null
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.band)
    }

    // ── Delete band dialog ───────────────────────────────────────────────────

    @Test
    fun `OnDeleteBandClicked shows confirm dialog`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandDetailUiEvent.OnDeleteBandClicked)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showDeleteBandConfirm)
    }

    @Test
    fun `OnDeleteBandDismiss hides confirm dialog`() = runTest(testDispatcher) {
        collectUiState()
        viewModel.onScreenEvent(BandDetailUiEvent.OnDeleteBandClicked)
        viewModel.onScreenEvent(BandDetailUiEvent.OnDeleteBandDismiss)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.showDeleteBandConfirm)
    }

    @Test
    fun `OnDeleteBandConfirmed calls deleteBand and emits NavigateBack`() =
        runTest(testDispatcher) {
            collectUiState()
            val events = mutableListOf<BandDetailEvent>()
            collectEvents(events)

            viewModel.onScreenEvent(BandDetailUiEvent.OnDeleteBandConfirmed)
            advanceUntilIdle()

            assertEquals(bandId, fakeRepo.deletedBandId)
            assertEquals(1, events.size)
            assertEquals(BandDetailEvent.NavigateBack, events.first())
        }

    // ── Navigation ───────────────────────────────────────────────────────────

    @Test
    fun `OnMembersSectionClicked emits NavigateToMembers`() = runTest(testDispatcher) {
        collectUiState()
        val events = mutableListOf<BandDetailEvent>()
        collectEvents(events)

        viewModel.onScreenEvent(BandDetailUiEvent.OnMembersSectionClicked)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(BandDetailEvent.NavigateToMembers, events.first())
    }
}
