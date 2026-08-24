package com.sunstar.streamcompass.presentation.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.usecase.AddWatchlistUseCase
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetWatchlistStreamUseCase
import com.sunstar.streamcompass.domain.usecase.RemoveWatchlistUseCase
import com.sunstar.streamcompass.presentation.core.filterIsInstance
import com.sunstar.streamcompass.presentation.core.tmdbId
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
import org.jetbrains.compose.resources.StringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.movie_row_now_playing
import streamcompass.architecture.presentation.generated.resources.movie_row_popular
import streamcompass.architecture.presentation.generated.resources.movie_row_top_rated
import streamcompass.architecture.presentation.generated.resources.movie_row_upcoming

class MovieViewModel(
    private val getSuggestionUseCase: GetSuggestionStreamUseCase,
    private val getWatchlistStreamUseCase: GetWatchlistStreamUseCase,
    private val addWatchlistUseCase: AddWatchlistUseCase,
    private val removeWatchlistUseCase: RemoveWatchlistUseCase,
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
                started = SharingStarted.Lazily,
                initialValue = State()
            )
    }

    fun toggleWatchlist(stream: MovieStream, isCurrentlyWatchlisted: Boolean) {
        viewModelScope.launch {
            eventChannel.send(
                Event.ToggleWatchlist(
                    stream = stream,
                    isCurrentlyWatchlisted = isCurrentlyWatchlisted
                )
            )
        }
    }

    private suspend fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> current.copy(
            rows = RowType.entries.associateWith { rowType ->
                getSuggestionUseCase(type = rowType.suggestionType)
                    .map { pagingData -> pagingData.filterIsInstance<MovieStream>() }
                    .cachedIn(viewModelScope)
            },
            watchlistedTmdbIds = getWatchlistStreamUseCase(streamType = StreamType.Movie)
                .map { streams -> streams.map { it.tmdbId }.toSet() },
        )

        is Event.ToggleWatchlist -> {
            if (event.isCurrentlyWatchlisted) {
                removeWatchlistUseCase(tmdbId = event.stream.tmdbId, streamType = StreamType.Movie)
            } else {
                addWatchlistUseCase(stream = event.stream)
            }
            current
        }
    }

    sealed interface Event {
        data object Initialize : Event
        data class ToggleWatchlist(val stream: MovieStream, val isCurrentlyWatchlisted: Boolean) :
            Event
    }

    sealed interface RowType {
        val id: String
        val titleRes: StringResource
        val suggestionType: SuggestionType.Movie

        data object NowPlaying : RowType {
            override val id: String = "now_playing"
            override val titleRes = Res.string.movie_row_now_playing
            override val suggestionType = SuggestionType.Movie.NowPlaying
        }

        data object Popular : RowType {
            override val id: String = "popular"
            override val titleRes = Res.string.movie_row_popular
            override val suggestionType = SuggestionType.Movie.Popular
        }

        data object TopRated : RowType {
            override val id: String = "top_rated"
            override val titleRes = Res.string.movie_row_top_rated
            override val suggestionType = SuggestionType.Movie.TopRated
        }

        data object Upcoming : RowType {
            override val id: String = "upcoming"
            override val titleRes = Res.string.movie_row_upcoming
            override val suggestionType = SuggestionType.Movie.Upcoming
        }

        companion object {
            val entries: List<RowType> = listOf(NowPlaying, Popular, TopRated, Upcoming)
        }
    }

    data class State(
        val rows: Map<RowType, Flow<PagingData<MovieStream>>> = emptyMap(),
        val watchlistedTmdbIds: Flow<Set<Int>> = emptyFlow(),
    )
}
