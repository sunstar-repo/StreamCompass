package com.sunstar.streamcompass

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

import com.sunstar.streamcompass.data.di.dataModule
import com.sunstar.streamcompass.presentation.StreamCompassApp
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
@Preview
fun App() {
    KoinApplication(configuration = koinConfiguration { modules(dataModule) }) {
        val greeting = remember { Greeting().greet() }
        StreamCompassApp(greeting = greeting)
    }
}
