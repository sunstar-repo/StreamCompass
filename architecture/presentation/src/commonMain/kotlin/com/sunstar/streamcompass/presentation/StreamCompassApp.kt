package com.sunstar.streamcompass.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory

import com.sunstar.streamcompass.presentation.dashboard.DashboardScreen

@Composable
fun StreamCompassApp() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .build()
    }
    MaterialTheme {
        DashboardScreen()
    }
}
