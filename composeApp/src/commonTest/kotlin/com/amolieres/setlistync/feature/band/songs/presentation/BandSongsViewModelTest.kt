package com.amolieres.setlistync.feature.band.songs.presentation

import androidx.lifecycle.SavedStateHandle
import com.amolieres.setlistync.core.domain.band.model.Band
import com.amolieres.setlistync.core.domain.band.usecase.ObserveBandUseCase
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.song.usecase.DeleteSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.ObserveSongsUseCase
import com.amolieres.setlistync.fake.FakeBandRepository
import com.amolieres.setlistync.fake.FakeSongNoteRepository
import com.amolieres.setlistync.fake.FakeSongRepository
import com.amolieres.setlistync.fake.FakeUserPreferencesRepository
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BandSongsViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private val fakeBandRepo = FakeBandRepository()
    private val fakeSongRepo = FakeSongRepository()
    private val fakeSongNoteRepo = FakeSongNoteRepository()
    private val fakePrefsRepo = FakeUserPreferencesRepository()
    private val bandId = "band-1"

    private val viewModel by lazy {
        BandSongsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("bandId" to bandId)),
            observeBand = ObserveBandUseCase(fakeBandRepo),
            observeSongs = ObserveSongsUseCase(fakeSongRepo),
            deleteSong = DeleteSongUseCase(fakeSongRepo, fakeSongNoteRepo),
            observeNotation = ObserveNotationUseCase(fakePrefsRepo)
        )
    }

    private val testBand = Band(id = bandId, name = "The Rocketeers")
    private val song1 = Song(id = SongId("s1"), title = "Summer Rain", durationSeconds = 213)
    private val song2 = Song(id = SongId("s2"), title = "Electric Nights", durationSeconds = 187)

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
        sink: MutableList<BandSongsEvent>
    ) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.event.collect { sink.add(it) }
        }
    }

    // ── Initial state ────────────────────────────────────────────────────────

    @Test
    fun `initial state is loading`() {
        assertTrue(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `initial state has empty songs list`() {
        assertTrue(viewModel.uiState.value.songs.isEmpty())
    }

    @Test
    fun `initial state has empty bandName`() {
        assertEquals("", viewModel.uiState.value.bandName)
    }

    // ── State from flows ─────────────────────────────────────────────────────

    @Test
    fun `when band and songs emit, isLoading becomes false`() = runTest(testDispatcher) {
        collectUiState()
        fakeBandRepo.bandFlow.value = testBand
        fakeSongRepo.songsFlow.value = listOf(song1)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `when band emits, bandName is updated`() = runTest(testDispatcher) {
        collectUiState()
        fakeBandRepo.bandFlow.value = testBand
        advanceUntilIdle()

        assertEquals("The Rocketeers", viewModel.uiState.value.bandName)
    }

    @Test
    fun `when band emits null, bandName is empty`() = runTest(testDispatcher) {
        collectUiState()
        fakeBandRepo.bandFlow.value = null
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.bandName)
    }

    @Test
    fun `when songs emit, songs list is updated`() = runTest(testDispatcher) {
        collectUiState()
        fakeSongRepo.songsFlow.value = listOf(song1, song2)
        advanceUntilIdle()

        assertEquals(listOf(song1, song2), viewModel.uiState.value.songs)
    }

    @Test
    fun `when songs are updated, state reflects the change reactively`() = runTest(testDispatcher) {
        collectUiState()
        fakeSongRepo.songsFlow.value = listOf(song1, song2)
        advanceUntilIdle()
        assertEquals(2, viewModel.uiState.value.songs.size)

        fakeSongRepo.songsFlow.value = listOf(song1)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.songs.size)
        assertEquals(song1, viewModel.uiState.value.songs.first())
    }

    // ── Navigation events ────────────────────────────────────────────────────

    @Test
    fun `OnAddSongClicked emits NavigateToNewSong`() = runTest(testDispatcher) {
        collectUiState()
        val events = mutableListOf<BandSongsEvent>()
        collectEvents(events)

        viewModel.onScreenEvent(BandSongsUiEvent.OnAddSongClicked)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(BandSongsEvent.NavigateToNewSong, events.first())
    }

    @Test
    fun `OnEditSongClicked emits NavigateToEditSong with correct songId`() = runTest(testDispatcher) {
        collectUiState()
        val events = mutableListOf<BandSongsEvent>()
        collectEvents(events)

        viewModel.onScreenEvent(BandSongsUiEvent.OnEditSongClicked(song1.id))
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(BandSongsEvent.NavigateToEditSong(song1.id), events.first())
    }

    // ── Delete song ──────────────────────────────────────────────────────────

    @Test
    fun `OnDeleteSongClicked calls deleteSong with correct bandId and songId`() =
        runTest(testDispatcher) {
            collectUiState()
            fakeSongRepo.songsFlow.value = listOf(song1, song2)
            advanceUntilIdle()

            viewModel.onScreenEvent(BandSongsUiEvent.OnDeleteSongClicked(song1.id))
            advanceUntilIdle()

            assertEquals(1, fakeSongRepo.deletedSongs.size)
            assertEquals(bandId to song1.id, fakeSongRepo.deletedSongs.first())
        }

    @Test
    fun `OnDeleteSongClicked removes song from state via reactive flow`() = runTest(testDispatcher) {
        collectUiState()
        fakeSongRepo.songsFlow.value = listOf(song1, song2)
        advanceUntilIdle()

        viewModel.onScreenEvent(BandSongsUiEvent.OnDeleteSongClicked(song1.id))
        advanceUntilIdle()

        val songs = viewModel.uiState.value.songs
        assertEquals(1, songs.size)
        assertEquals(song2, songs.first())
    }
}
