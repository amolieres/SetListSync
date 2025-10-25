package com.amolieres.setlistync

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
            val snackbarHostState = remember { SnackbarHostState() }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("SetListSync") }
                    )
                },
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
            ) { innerPadding ->
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}