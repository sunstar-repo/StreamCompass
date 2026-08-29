package com.sunstar.streamcompass.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.presentation.StreamCompassPresenterViewModel

@Composable
fun SplashScreen(
    state: StreamCompassPresenterViewModel.State,
    onLoading: @Composable () -> Unit,
    onFailure: @Composable (message: String) -> Unit,
    onSucceed: @Composable (apiKey: ApiKey) -> Unit
) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            StreamCompassPresenterViewModel.State.Idle,
            StreamCompassPresenterViewModel.State.Loading -> onLoading()

            is StreamCompassPresenterViewModel.State.Failure -> onFailure(state.message)
            is StreamCompassPresenterViewModel.State.Succeed -> onSucceed(state.apiKey)
        }
    }
}
