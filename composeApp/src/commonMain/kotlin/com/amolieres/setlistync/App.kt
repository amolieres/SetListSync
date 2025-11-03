package com.amolieres.setlistync

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.amolieres.setlistync.app.navigation.AppNavGraph
import com.amolieres.setlistync.common.di.repositoryModule
import com.amolieres.setlistync.common.di.useCaseModule
import com.amolieres.setlistync.common.di.viewModelModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    KoinApplication(application = {
        modules(
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