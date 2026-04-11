package com.amolieres.setlistync

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.amolieres.setlistync.app.navigation.AppNavGraph
import com.amolieres.setlistync.app.theme.AppTheme
import com.amolieres.setlistync.common.di.networkModule
import com.amolieres.setlistync.common.di.platformModule
import com.amolieres.setlistync.common.di.repositoryModule
import com.amolieres.setlistync.common.di.useCaseModule
import com.amolieres.setlistync.common.di.viewModelModule
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinConfiguration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(koinAppDeclaration: KoinAppDeclaration? = null) {
    KoinApplication(configuration = koinConfiguration(declaration = {
        koinAppDeclaration?.invoke(this)
        modules(
            platformModule,
            networkModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }), content = {
        AppTheme {
            val navController = rememberNavController()

            Box(modifier = Modifier.fillMaxSize()) {
                AppNavGraph(
                    navController = navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    })
}