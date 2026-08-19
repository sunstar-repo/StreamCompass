package com.sunstar.streamcompass.presentation.allstreams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.usecase.GetHistoryStreamUseCase
import com.sunstar.streamcompass.domain.usecase.GetSuggestionStreamUseCase
import com.sunstar.streamcompass.presentation.home.HomeViewModel
import com.sunstar.streamcompass.presentation.movie.MovieViewModel
import com.sunstar.streamcompass.presentation.tv.TvViewModel
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

class AllStreamsViewModel(
    private val rowId: String,
    private val streamType: StreamType,
    private val getSuggestionUseCase: GetSuggestionStreamUseCase,
    private val getHistoryStreamUseCase: GetHistoryStreamUseCase,
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

    private fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> current.copy(streams = fetchStreams())
    }

    // rowId는 Movie/Tv/Home 각 화면 Row의 SuggestionType과 1:1로 대응 — 대응되는 게 없으면
    // (Home의 History Row) Room 기반 시청 기록을 그대로 전체보기 대상으로 삼는다.
    private fun fetchStreams(): Flow<PagingData<Stream>> {
        val suggestionType = findSuggestionType(rowId = rowId, streamType = streamType)
        return if (null != suggestionType) {
            getSuggestionUseCase(type = suggestionType).cachedIn(viewModelScope)
        } else {
            getHistoryStreamUseCase(streamType = streamType).map { PagingData.from(it) }
        }
    }

    sealed interface Event {
        data object Initialize : Event
    }

    data class State(
        val streams: Flow<PagingData<Stream>> = emptyFlow(),
    )

    private fun findSuggestionType(rowId: String, streamType: StreamType): SuggestionType? =
        when (streamType) {
            StreamType.Movie -> when (rowId) {
                MovieViewModel.RowType.NowPlaying.id -> SuggestionType.Movie.NowPlaying
                MovieViewModel.RowType.Popular.id -> SuggestionType.Movie.Popular
                MovieViewModel.RowType.TopRated.id -> SuggestionType.Movie.TopRated
                MovieViewModel.RowType.Upcoming.id -> SuggestionType.Movie.Upcoming
                HomeViewModel.RowType.NewMovies.id -> SuggestionType.Movie.NewReleases
                else -> null
            }

            StreamType.Tv -> when (rowId) {
                TvViewModel.RowType.AiringToday.id -> SuggestionType.Tv.AiringToday
                TvViewModel.RowType.OnTheAir.id -> SuggestionType.Tv.OnTheAir
                TvViewModel.RowType.Popular.id -> SuggestionType.Tv.Popular
                TvViewModel.RowType.TopRated.id -> SuggestionType.Tv.TopRated
                HomeViewModel.RowType.NewTv.id -> SuggestionType.Tv.NewReleases
                else -> null
            }
        }
}


