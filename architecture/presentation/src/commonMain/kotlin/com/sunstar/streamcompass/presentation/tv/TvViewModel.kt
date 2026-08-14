package com.sunstar.streamcompass.presentation.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.TvSuggestionType
import com.sunstar.streamcompass.domain.usecase.GetTvSuggestionStreamUseCase
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
import streamcompass.architecture.presentation.generated.resources.tv_row_airing_today
import streamcompass.architecture.presentation.generated.resources.tv_row_on_the_air
import streamcompass.architecture.presentation.generated.resources.tv_row_popular
import streamcompass.architecture.presentation.generated.resources.tv_row_top_rated

class TvViewModel(
    private val getTvSuggestionUseCase: GetTvSuggestionStreamUseCase,
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
                getTvSuggestionUseCase(type = rowType.suggestionType).cachedIn(viewModelScope)
            }
        )
    }

    sealed interface Event {
        data object Initialize : Event

    }

    sealed interface RowType {
        val titleRes: StringResource
        val suggestionType: TvSuggestionType

        data object AiringToday : RowType {
            override val titleRes = Res.string.tv_row_airing_today
            override val suggestionType = TvSuggestionType.AiringToday
        }

        data object OnTheAir : RowType {
            override val titleRes = Res.string.tv_row_on_the_air
            override val suggestionType = TvSuggestionType.OnTheAir
        }

        data object Popular : RowType {
            override val titleRes = Res.string.tv_row_popular
            override val suggestionType = TvSuggestionType.Popular
        }

        data object TopRated : RowType {
            override val titleRes = Res.string.tv_row_top_rated
            override val suggestionType = TvSuggestionType.TopRated
        }

        companion object {
            val entries: List<RowType> = listOf(AiringToday, OnTheAir, Popular, TopRated)
        }
    }

    data class State(
        val rows: Map<RowType, Flow<PagingData<TvStream>>> = emptyMap()
    )
}
