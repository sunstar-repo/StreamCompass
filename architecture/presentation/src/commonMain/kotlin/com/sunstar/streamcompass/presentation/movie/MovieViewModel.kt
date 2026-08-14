package com.sunstar.streamcompass.presentation.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

class MovieViewModel(
    private val getSuggestionUseCase: GetSuggestionStreamUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = eventChannel.receiveAsFlow()
            .onStart {
                emit(Event.Initialize)
            }
            .runningFold(
                initial = State(),
                operation = ::handleEvent
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = State()
            )
    }

    private fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> current.copy(
            nowPlayings = getSuggestionUseCase(
                type = SuggestionType.NowPlaying
            ).cachedIn(viewModelScope),
            upcommings = getSuggestionUseCase(
                type = SuggestionType.Upcoming
            ).cachedIn(viewModelScope)
        )
    }

    sealed interface Event {
        data object Initialize : Event

    }

    data class State(
        val nowPlayings: Flow<PagingData<MovieStream>> = flowOf(),
        val upcommings: Flow<PagingData<MovieStream>> = flowOf()
    )
}
