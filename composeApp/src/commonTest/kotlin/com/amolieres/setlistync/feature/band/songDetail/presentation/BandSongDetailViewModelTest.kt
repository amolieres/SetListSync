package com.amolieres.setlistync.feature.band.songDetail.presentation

import androidx.lifecycle.SavedStateHandle
import com.amolieres.setlistync.core.domain.song.model.Song
import com.amolieres.setlistync.core.domain.song.model.SongId
import com.amolieres.setlistync.core.domain.preferences.ObserveNotationUseCase
import com.amolieres.setlistync.core.domain.song.model.SongKey
import com.amolieres.setlistync.core.domain.song.usecase.AddSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.GetSongAudioFeaturesUseCase
import com.amolieres.setlistync.core.domain.song.usecase.GetSongUseCase
import com.amolieres.setlistync.core.domain.song.usecase.SearchSongsUseCase
import com.amolieres.setlistync.core.domain.song.usecase.UpdateSongUseCase
import com.amolieres.setlistync.fake.FakeSongCatalogRepository
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class BandSongDetailViewModelTest {

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private val fakeSongRepo = FakeSongRepository()
    private val fakeSongCatalogRepo = FakeSongCatalogRepository()
    private val fakePrefsRepo = FakeUserPreferencesRepository()
    private val bandId = "band-1"

    // Song with 3 min 33 sec, key Am, tempo 120, one external link
    private val existingSong = Song(
        id = SongId("s1"),
        title = "Summer Rain",
        durationSeconds = 213,
        key = SongKey.A_MINOR,
        tempo = 120,
        externalLinks = listOf("https://youtube.com/abc")
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ── ViewModel factory ─────────────────────────────────────────────────────

    private fun buildViewModel(songId: String? = null): BandSongDetailViewModel {
        val args = mutableMapOf<String, String>("bandId" to bandId)
        if (songId != null) args["songId"] = songId
        return BandSongDetailViewModel(
            savedStateHandle = SavedStateHandle(args),
            getSong = GetSongUseCase(fakeSongRepo),
            addSong = AddSongUseCase(fakeSongRepo),
            updateSong = UpdateSongUseCase(fakeSongRepo),
            searchSongs = SearchSongsUseCase(fakeSongCatalogRepo),
            getAudioFeatures = GetSongAudioFeaturesUseCase(fakeSongCatalogRepo),
            observeNotation = ObserveNotationUseCase(fakePrefsRepo)
        )
    }

    private fun kotlinx.coroutines.test.TestScope.collectUiState(vm: BandSongDetailViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.uiState.collect {}
        }
    }

    private fun kotlinx.coroutines.test.TestScope.collectEvents(
        vm: BandSongDetailViewModel,
        sink: MutableList<BandSongDetailEvent>
    ) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.event.collect { sink.add(it) }
        }
    }

    // ── Create mode — initial state ───────────────────────────────────────────

    @Test
    fun `in create mode, isEditMode is false`() {
        assertFalse(buildViewModel().isEditMode)
    }

    @Test
    fun `in create mode, initial state has empty fields and isLoading false`() {
        val state = buildViewModel().uiState.value
        assertFalse(state.isLoading)
        assertEquals("", state.title)
        assertEquals("", state.minutes)
        assertEquals("", state.seconds)
        assertNull(state.key)
        assertEquals("", state.tempo)
    }

    // ── Field change events ───────────────────────────────────────────────────

    @Test
    fun `OnTitleChanged updates title in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("My Song"))
        advanceUntilIdle()
        assertEquals("My Song", vm.uiState.value.title)
    }

    @Test
    fun `OnMinutesChanged updates minutes in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        vm.onScreenEvent(BandSongDetailUiEvent.OnMinutesChanged("4"))
        advanceUntilIdle()
        assertEquals("4", vm.uiState.value.minutes)
    }

    @Test
    fun `OnSecondsChanged updates seconds in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        vm.onScreenEvent(BandSongDetailUiEvent.OnSecondsChanged("30"))
        advanceUntilIdle()
        assertEquals("30", vm.uiState.value.seconds)
    }

    @Test
    fun `OnKeyChanged updates key in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        vm.onScreenEvent(BandSongDetailUiEvent.OnKeyChanged(SongKey.C_SHARP_MAJOR))
        advanceUntilIdle()
        assertEquals(SongKey.C_SHARP_MAJOR, vm.uiState.value.key)
    }

    @Test
    fun `OnTempoChanged updates tempo in state`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        vm.onScreenEvent(BandSongDetailUiEvent.OnTempoChanged("128"))
        advanceUntilIdle()
        assertEquals("128", vm.uiState.value.tempo)
    }

    // ── Back navigation ───────────────────────────────────────────────────────

    @Test
    fun `OnBackClicked emits NavigateBack`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        val events = mutableListOf<BandSongDetailEvent>()
        collectEvents(vm, events)

        vm.onScreenEvent(BandSongDetailUiEvent.OnBackClicked)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(BandSongDetailEvent.NavigateBack, events.first())
    }

    // ── Create mode — save ────────────────────────────────────────────────────

    @Test
    fun `OnSaveClicked with blank title does not call addSong`() = runTest(testDispatcher) {
        val vm = buildViewModel()
        collectUiState(vm)
        vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("   "))
        vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
        advanceUntilIdle()

        assertTrue(fakeSongRepo.addedSongs.isEmpty())
    }

    @Test
    fun `OnSaveClicked in create mode calls addSong and emits NavigateBack`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            collectUiState(vm)
            val events = mutableListOf<BandSongDetailEvent>()
            collectEvents(vm, events)

            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("My Song"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            assertEquals(1, fakeSongRepo.addedSongs.size)
            assertEquals(1, events.size)
            assertEquals(BandSongDetailEvent.NavigateBack, events.first())
        }

    @Test
    fun `OnSaveClicked in create mode stores correct bandId and trims title`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            collectUiState(vm)
            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("  My Song  "))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            val (savedBandId, savedSong) = fakeSongRepo.addedSongs.first()
            assertEquals(bandId, savedBandId)
            assertEquals("My Song", savedSong.title)
        }

    @Test
    fun `OnSaveClicked in create mode computes durationSeconds from minutes and seconds`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            collectUiState(vm)
            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("My Song"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnMinutesChanged("3"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSecondsChanged("45"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            val (_, savedSong) = fakeSongRepo.addedSongs.first()
            assertEquals(3 * 60 + 45, savedSong.durationSeconds)
        }

    @Test
    fun `OnSaveClicked in create mode passes null key when key field is blank`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            collectUiState(vm)
            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("My Song"))
            // key intentionally left blank
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            val (_, savedSong) = fakeSongRepo.addedSongs.first()
            assertNull(savedSong.key)
        }

    @Test
    fun `OnSaveClicked in create mode passes null tempo when tempo field is blank`() =
        runTest(testDispatcher) {
            val vm = buildViewModel()
            collectUiState(vm)
            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("My Song"))
            // tempo intentionally left blank
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            val (_, savedSong) = fakeSongRepo.addedSongs.first()
            assertNull(savedSong.tempo)
        }

    // ── Edit mode — loading ───────────────────────────────────────────────────

    @Test
    fun `in edit mode, isEditMode is true`() {
        assertTrue(buildViewModel(songId = existingSong.id.value).isEditMode)
    }

    @Test
    fun `in edit mode, fields are pre-filled after song is loaded`() = runTest(testDispatcher) {
        fakeSongRepo.songsFlow.value = listOf(existingSong)
        val vm = buildViewModel(songId = existingSong.id.value)
        collectUiState(vm)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(existingSong.title, state.title)
        assertEquals(SongKey.A_MINOR, state.key)
        assertEquals("120", state.tempo)
        assertFalse(state.isLoading)
    }

    @Test
    fun `in edit mode, durationSeconds is split into minutes and seconds`() =
        runTest(testDispatcher) {
            // existingSong.durationSeconds = 213 → 3 min 33 sec
            fakeSongRepo.songsFlow.value = listOf(existingSong)
            val vm = buildViewModel(songId = existingSong.id.value)
            collectUiState(vm)
            advanceUntilIdle()

            val state = vm.uiState.value
            assertEquals("3", state.minutes)
            assertEquals("33", state.seconds)
        }

    @Test
    fun `in edit mode with unknown songId, isLoading becomes false`() = runTest(testDispatcher) {
        // songsFlow is empty — getSong returns null
        val vm = buildViewModel(songId = "unknown-id")
        collectUiState(vm)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
    }

    // ── Edit mode — save ──────────────────────────────────────────────────────

    @Test
    fun `OnSaveClicked in edit mode calls updateSong with modified title and duration`() =
        runTest(testDispatcher) {
            fakeSongRepo.songsFlow.value = listOf(existingSong)
            val vm = buildViewModel(songId = existingSong.id.value)
            collectUiState(vm)
            advanceUntilIdle()

            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("New Title"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnMinutesChanged("5"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSecondsChanged("0"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            assertEquals(1, fakeSongRepo.updatedSongs.size)
            val (savedBandId, updatedSong) = fakeSongRepo.updatedSongs.first()
            assertEquals(bandId, savedBandId)
            assertEquals("New Title", updatedSong.title)
            assertEquals(5 * 60, updatedSong.durationSeconds)
        }

    @Test
    fun `OnSaveClicked in edit mode preserves song id and externalLinks`() =
        runTest(testDispatcher) {
            fakeSongRepo.songsFlow.value = listOf(existingSong)
            val vm = buildViewModel(songId = existingSong.id.value)
            collectUiState(vm)
            advanceUntilIdle()

            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("New Title"))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            val (_, updatedSong) = fakeSongRepo.updatedSongs.first()
            assertEquals(existingSong.id, updatedSong.id)
            assertEquals(existingSong.externalLinks, updatedSong.externalLinks)
        }

    @Test
    fun `OnSaveClicked in edit mode emits NavigateBack`() = runTest(testDispatcher) {
        fakeSongRepo.songsFlow.value = listOf(existingSong)
        val vm = buildViewModel(songId = existingSong.id.value)
        collectUiState(vm)
        val events = mutableListOf<BandSongDetailEvent>()
        collectEvents(vm, events)
        advanceUntilIdle()

        vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("New Title"))
        vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(BandSongDetailEvent.NavigateBack, events.first())
    }

    @Test
    fun `OnSaveClicked in edit mode with blank title does not call updateSong`() =
        runTest(testDispatcher) {
            fakeSongRepo.songsFlow.value = listOf(existingSong)
            val vm = buildViewModel(songId = existingSong.id.value)
            collectUiState(vm)
            advanceUntilIdle()

            vm.onScreenEvent(BandSongDetailUiEvent.OnTitleChanged("   "))
            vm.onScreenEvent(BandSongDetailUiEvent.OnSaveClicked)
            advanceUntilIdle()

            assertTrue(fakeSongRepo.updatedSongs.isEmpty())
        }
}
