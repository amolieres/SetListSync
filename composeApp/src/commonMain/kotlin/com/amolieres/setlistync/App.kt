package com.amolieres.setlistync

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.amolieres.setlistync.app.navigation.AppNavGraph
import com.amolieres.setlistync.common.di.platformModule
import com.amolieres.setlistync.common.di.repositoryModule
import com.amolieres.setlistync.common.di.useCaseModule
import com.amolieres.setlistync.common.di.viewModelModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(application = {
        koinAppDeclaration?.invoke(this)
        modules(
            platformModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }) {
        MaterialTheme {
            val navController = rememberNavController()

            Box(modifier = Modifier.fillMaxSize()) {
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}