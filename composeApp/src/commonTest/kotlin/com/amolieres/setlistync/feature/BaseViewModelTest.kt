package com.amolieres.setlistync.feature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest {

    protected val testDispatcher = StandardTestDispatcher()
    protected val testScope = TestScope(testDispatcher)

    @BeforeTest
    fun setupDispatcher() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDownDispatcher() {
        Dispatchers.resetMain()
    }

    protected fun advanceUntilIdle() {
        testScope.advanceUntilIdle()
    }

    /**
     * Collect all flow events
     */
    protected fun <T> collectEvents(
        flow: kotlinx.coroutines.flow.Flow<T>,
        timeout: Duration = 2.seconds
    ): List<T> {
        val events = mutableListOf<T>()
        val job = testScope.launch { flow.collect { events.add(it) } }
        testScope.advanceTimeBy(timeout.inWholeMilliseconds)
        job.cancel()
        return events
    }
}
