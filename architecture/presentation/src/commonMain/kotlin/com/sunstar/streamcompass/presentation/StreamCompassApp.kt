package com.sunstar.streamcompass.presentation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.presentation.main.MainScreen
import com.sunstar.streamcompass.presentation.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StreamCompassApp(
    viewModel: StreamCompassAppViewModel = koinViewModel(),
    onApiKeyReady: @Composable (apiKey: ApiKey) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    MaterialTheme {
        SplashScreen(
            state = state,
            onLoading = { CircularProgressIndicator() },
            onFailure = { message -> Text(message) },
        ) { apiKey ->
            onApiKeyReady(apiKey)
            MainScreen()
        }
    }
}
