package com.sunstar.streamcompass.presentation.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.presentation.StreamCompassAppViewModel

@Composable
fun SplashScreen(
    state: StreamCompassAppViewModel.State,
    onLoading: @Composable () -> Unit,
    onFailure: @Composable (message: String) -> Unit,
    onSucceed: @Composable (apiKey: ApiKey) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (state) {
            StreamCompassAppViewModel.State.Idle,
            StreamCompassAppViewModel.State.Loading -> onLoading()
            is StreamCompassAppViewModel.State.Failure -> onFailure(state.message)
            is StreamCompassAppViewModel.State.Succeed -> onSucceed(state.apiKey)
        }
    }
}
