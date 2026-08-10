package com.sunstar.streamcompass.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunstar.streamcompass.core.Log
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.domain.usecase.InitializeAppUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StreamCompassAppViewModel(
    private val initializeAppUseCase: InitializeAppUseCase,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow<State>(State.Idle)
    val stateFlow: StateFlow<State> = _stateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _stateFlow.value = State.Loading

            val apiKey = withContext(Dispatchers.IO) { initializeAppUseCase() }
            _stateFlow.value =
                if (apiKey.tmdbKey.isEmpty()) {
                    State.Failure("TMDB initialize failed")
                } else if (apiKey.saKey.isEmpty()) {
                    State.Failure("SA initialize failed")
                } else {
                    State.Succeed(apiKey = apiKey)
                }
        }
    }

    sealed interface State {
        data object Idle : State

        data object Loading : State

        data class Succeed(val apiKey: ApiKey) : State

        data class Failure(val message: String) : State
    }
}
