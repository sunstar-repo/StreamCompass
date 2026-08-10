package com.sunstar.streamcompass.presentation.streamdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.usecase.GetStreamDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StreamDetailViewModel(
    private val tmdbId: Int,
    private val getStreamDetailUseCase: GetStreamDetailUseCase,
) : ViewModel() {
    private val _stateFlow = MutableStateFlow<State>(State.Loading)
    val stateFlow: StateFlow<State> = _stateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _stateFlow.value =
                State.Succeed(
                    streamDetail = getStreamDetailUseCase(tmdbId = tmdbId, locale = DEFAULT_LOCALE),
                )
        }
    }

    sealed interface State {
        data object Loading : State
        data class Succeed(val streamDetail: MovieStreamDetail) : State
    }

    private companion object {
        const val DEFAULT_LOCALE = "en-US"
    }
}
