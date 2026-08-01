package com.sunstar.streamcompass.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import kotlinx.coroutines.flow.Flow

class DashboardViewModel(
    getSuggestionStreamUseCase: GetSuggestionStreamUseCase,
) : ViewModel() {
    val nowPlayingStreams: Flow<PagingData<Stream>> =
        getSuggestionStreamUseCase(SuggestionType.NowPlaying)
            .cachedIn(viewModelScope)
}
