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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import org.jetbrains.compose.resources.StringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.movie_row_now_playing
import streamcompass.architecture.presentation.generated.resources.movie_row_popular
import streamcompass.architecture.presentation.generated.resources.movie_row_top_rated
import streamcompass.architecture.presentation.generated.resources.movie_row_upcoming

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
            rows = RowType.entries.associateWith { rowType ->
                getSuggestionUseCase(type = rowType.suggestionType).cachedIn(viewModelScope)
            }
        )
    }

    sealed interface Event {
        data object Initialize : Event

    }

    sealed interface RowType {
        val titleRes: StringResource
        val suggestionType: SuggestionType

        data object NowPlaying : RowType {
            override val titleRes = Res.string.movie_row_now_playing
            override val suggestionType = SuggestionType.NowPlaying
        }

        data object Popular : RowType {
            override val titleRes = Res.string.movie_row_popular
            override val suggestionType = SuggestionType.Popular
        }

        data object TopRated : RowType {
            override val titleRes = Res.string.movie_row_top_rated
            override val suggestionType = SuggestionType.TopRated
        }

        data object Upcoming : RowType {
            override val titleRes = Res.string.movie_row_upcoming
            override val suggestionType = SuggestionType.Upcoming
        }

        companion object {
            val entries: List<RowType> = listOf(NowPlaying, Popular, TopRated, Upcoming)
        }
    }

    data class State(
        val rows: Map<RowType, Flow<PagingData<MovieStream>>> = emptyMap()
    )
}
