package com.sunstar.streamcompass.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.usecase.GetHistoryStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetTrendingStreamUseCase
import com.sunstar.streamcompass.domain.usecase.RemoveHistoryUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val removeHistoryUseCase: RemoveHistoryUseCase,
    private val getTrendingStreamUseCase: GetTrendingStreamUseCase,
    getHistoryStreamUseCase: GetHistoryStreamUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = merge(
            eventChannel.receiveAsFlow().onStart { emit(Event.Initialize) },
            getHistoryStreamUseCase(streamType = StreamType.Movie)
                .map { Event.MovieHistoryChanged(items = it.filterIsInstance<MovieStream>()) },
            getHistoryStreamUseCase(streamType = StreamType.Tv)
                .map { Event.TvHistoryChanged(items = it.filterIsInstance<TvStream>()) },
        )
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

    fun removeMovieHistory(tmdbId: Int) {
        viewModelScope.launch {
            eventChannel.send(Event.RemoveMovieHistory(tmdbId = tmdbId))
        }
    }

    fun removeTvHistory(tmdbId: Int) {
        viewModelScope.launch {
            eventChannel.send(Event.RemoveTvHistory(tmdbId = tmdbId))
        }
    }

    private suspend fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> current.copy(trendingStreams = getTrendingStreamUseCase())
        is Event.MovieHistoryChanged -> current.copy(movieHistoryStreams = event.items)
        is Event.TvHistoryChanged -> current.copy(tvHistoryStreams = event.items)
        is Event.RemoveMovieHistory -> {
            removeHistoryUseCase(tmdbId = event.tmdbId, streamType = StreamType.Movie)
            current
        }

        is Event.RemoveTvHistory -> {
            removeHistoryUseCase(tmdbId = event.tmdbId, streamType = StreamType.Tv)
            current
        }
    }

    sealed interface Event {
        data object Initialize : Event
        data class MovieHistoryChanged(val items: List<MovieStream>) : Event
        data class TvHistoryChanged(val items: List<TvStream>) : Event
        data class RemoveMovieHistory(val tmdbId: Int) : Event
        data class RemoveTvHistory(val tmdbId: Int) : Event
    }

    data class State(
        val trendingStreams: List<Stream> = emptyList(),
        val movieHistoryStreams: List<MovieStream> = emptyList(),
        val tvHistoryStreams: List<TvStream> = emptyList(),
    )

    sealed interface RowType {
        val id: String

        data object Trending : RowType {
            override val id: String = "trending"
        }

        data object MovieHistory : RowType {
            override val id: String = "movie_history"
        }

        data object TvHistory : RowType {
            override val id: String = "tv_history"
        }
    }
}
