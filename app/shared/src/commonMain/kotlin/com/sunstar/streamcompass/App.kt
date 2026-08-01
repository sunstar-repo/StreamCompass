package com.sunstar.streamcompass

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.sunstar.streamcompass.data.di.dataModule
import com.sunstar.streamcompass.presentation.StreamCompassApp
import com.sunstar.streamcompass.presentation.di.presentationModule
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(dataModule, presentationModule) }) {
        StreamCompassApp()
    }
}
