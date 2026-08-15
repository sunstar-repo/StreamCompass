package com.sunstar.streamcompass.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.domain.model.ThemeMode
import com.sunstar.streamcompass.presentation.main.MainScreen
import com.sunstar.streamcompass.presentation.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StreamCompassApp(
    viewModel: StreamCompassAppViewModel = koinViewModel(),
    onAppReady: @Composable (apiKey: ApiKey) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()
    val themeMode by viewModel.themeModeFlow.collectAsState()

    val isDarkTheme = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        SplashScreen(
            state = state,
            onLoading = { CircularProgressIndicator() },
            onFailure = { message -> Text(message) },
        ) { apiKey ->
            onAppReady(apiKey)
            MainScreen()
        }
    }
}
