package com.sunstar.streamcompass.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sunstar.streamcompass.domain.model.ApiKey
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StreamCompassApp(
    viewModel: StreamCompassAppViewModel = koinViewModel(),
    content: @Composable (ApiKey) -> Unit,
) {
    val state by viewModel.stateFlow.collectAsState()

    MaterialTheme {
        when (val current = state) {
            StreamCompassAppViewModel.State.Idle,
            StreamCompassAppViewModel.State.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is StreamCompassAppViewModel.State.Succeed -> content(current.apiKey)

            is StreamCompassAppViewModel.State.Failure -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = current.message) // TODO 적당한 message 로 변경 필요
                }
            }
        }
    }
}



