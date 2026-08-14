package com.sunstar.streamcompass.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.usecase.GetTrendingStreamUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTrendingStreamUseCase: GetTrendingStreamUseCase,
) : ViewModel() {
    private val _trendingStreams = MutableStateFlow<List<Stream>>(emptyList())
    val trendingStreams: StateFlow<List<Stream>> = _trendingStreams.asStateFlow()

    init {
        viewModelScope.launch {
            _trendingStreams.value = getTrendingStreamUseCase()
        }
    }
}
