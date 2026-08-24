package com.sunstar.streamcompass.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.usecase.AddWatchlistUseCase
import com.sunstar.streamcompass.domain.usecase.GetHistoryStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetTrendingStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetWatchlistStreamUseCase
import com.sunstar.streamcompass.domain.usecase.RemoveHistoryUseCase
import com.sunstar.streamcompass.domain.usecase.RemoveWatchlistUseCase
import com.sunstar.streamcompass.presentation.core.filterIsInstance
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val removeHistoryUseCase: RemoveHistoryUseCase,
    private val getTrendingStreamUseCase: GetTrendingStreamUseCase,
    private val getSuggestionUseCase: GetSuggestionStreamUseCase,
    private val getHistoryStreamUseCase: GetHistoryStreamUseCase,
    private val getWatchlistStreamUseCase: GetWatchlistStreamUseCase,
    private val addWatchlistUseCase: AddWatchlistUseCase,
    private val removeWatchlistUseCase: RemoveWatchlistUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = eventChannel.receiveAsFlow()
            .onStart { emit(Event.Initialize) }
            .runningFold(
                initial = State(),
                operation = ::handleEvent
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
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

    fun toggleMovieWatchlist(stream: MovieStream, isCurrentlyWatchlisted: Boolean) {
        viewModelScope.launch {
            eventChannel.send(
                Event.ToggleMovieWatchlist(
                    stream = stream,
                    isCurrentlyWatchlisted = isCurrentlyWatchlisted
                )
            )
        }
    }

    fun toggleTvWatchlist(stream: TvStream, isCurrentlyWatchlisted: Boolean) {
        viewModelScope.launch {
            eventChannel.send(
                Event.ToggleTvWatchlist(
                    stream = stream,
                    isCurrentlyWatchlisted = isCurrentlyWatchlisted
                )
            )
        }
    }

    private suspend fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> current.copy(
            trendingStreams = getTrendingStreamUseCase(),
            newMovieStreams = getSuggestionUseCase(type = SuggestionType.Movie.NewReleases)
                .map { it.filterIsInstance<MovieStream>() }
                .cachedIn(viewModelScope),
            newTvStreams = getSuggestionUseCase(type = SuggestionType.Tv.NewReleases)
                .map { it.filterIsInstance<TvStream>() }
                .cachedIn(viewModelScope),
            movieHistoryStreams = getHistoryStreamUseCase(streamType = StreamType.Movie)
                .map { it.filterIsInstance<MovieStream>() },
            tvHistoryStreams = getHistoryStreamUseCase(streamType = StreamType.Tv)
                .map { it.filterIsInstance<TvStream>() },
            movieWatchlistStreams = getWatchlistStreamUseCase(streamType = StreamType.Movie)
                .map { it.filterIsInstance<MovieStream>() },
            tvWatchlistStreams = getWatchlistStreamUseCase(streamType = StreamType.Tv)
                .map { it.filterIsInstance<TvStream>() },
        )

        is Event.RemoveMovieHistory -> {
            removeHistoryUseCase(tmdbId = event.tmdbId, streamType = StreamType.Movie)
            current
        }

        is Event.RemoveTvHistory -> {
            removeHistoryUseCase(tmdbId = event.tmdbId, streamType = StreamType.Tv)
            current
        }

        is Event.ToggleMovieWatchlist -> {
            if (event.isCurrentlyWatchlisted) {
                removeWatchlistUseCase(tmdbId = event.stream.tmdbId, streamType = StreamType.Movie)
            } else {
                addWatchlistUseCase(stream = event.stream)
            }
            current
        }

        is Event.ToggleTvWatchlist -> {
            if (event.isCurrentlyWatchlisted) {
                removeWatchlistUseCase(tmdbId = event.stream.tmdbId, streamType = StreamType.Tv)
            } else {
                addWatchlistUseCase(stream = event.stream)
            }
            current
        }
    }

    sealed interface Event {
        data object Initialize : Event
        data class RemoveMovieHistory(val tmdbId: Int) : Event
        data class RemoveTvHistory(val tmdbId: Int) : Event
        data class ToggleMovieWatchlist(
            val stream: MovieStream,
            val isCurrentlyWatchlisted: Boolean
        ) : Event

        data class ToggleTvWatchlist(val stream: TvStream, val isCurrentlyWatchlisted: Boolean) :
            Event
    }

    data class State(
        val trendingStreams: List<Stream> = emptyList(),
        val newMovieStreams: Flow<PagingData<MovieStream>> = emptyFlow(),
        val newTvStreams: Flow<PagingData<TvStream>> = emptyFlow(),
        val movieHistoryStreams: Flow<List<MovieStream>> = emptyFlow(),
        val tvHistoryStreams: Flow<List<TvStream>> = emptyFlow(),
        val movieWatchlistStreams: Flow<List<MovieStream>> = emptyFlow(),
        val tvWatchlistStreams: Flow<List<TvStream>> = emptyFlow(),
    )

    sealed interface RowType {
        val id: String

        data object Trending : RowType {
            override val id: String = "trending"
        }

        data object NewMovies : RowType {
            override val id: String = "new_movies"
        }

        data object NewTv : RowType {
            override val id: String = "new_tv"
        }

        data object MovieHistory : RowType {
            override val id: String = "movie_history"
        }

        data object TvHistory : RowType {
            override val id: String = "tv_history"
        }

        data object MovieWatchlist : RowType {
            override val id: String = "movie_watchlist"
        }

        data object TvWatchlist : RowType {
            override val id: String = "tv_watchlist"
        }
    }
}
