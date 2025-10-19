package com.amolieres.setlistync.common.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    val Main: CoroutineDispatcher
    val Default: CoroutineDispatcher
}

object DefaultDispatcherProvider : DispatcherProvider {
    override val Main: CoroutineDispatcher = Dispatchers.Main
    override val Default: CoroutineDispatcher = Dispatchers.Default
}
