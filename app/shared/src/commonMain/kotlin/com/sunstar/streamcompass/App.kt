package com.sunstar.streamcompass

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.sunstar.streamcompass.data.di.dataModule
import com.sunstar.streamcompass.data.di.remoteConfigModule
import com.sunstar.streamcompass.data.di.settingModule
import com.sunstar.streamcompass.presentation.StreamCompassApp
import com.sunstar.streamcompass.presentation.di.presentationModule
import org.koin.compose.KoinApplication
import org.koin.compose.module.rememberKoinModules
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    KoinApplication(
        configuration = koinConfiguration {
            modules(
                remoteConfigModule,
                settingModule,
                presentationModule
            )
        }
    ) {
        StreamCompassApp(
            onAppReady = { apiKey ->
                rememberKoinModules {
                    listOf(dataModule(apiKey = apiKey))
                }
            }
        )
    }
}
