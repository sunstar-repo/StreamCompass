package com.sunstar.streamcompass.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.usecase.GetRecommendationsUseCase
import com.sunstar.streamcompass.domain.usecase.GetReviewsUseCase
import com.sunstar.streamcompass.domain.usecase.GetStreamDetailUseCase
import com.sunstar.streamcompass.domain.usecase.RecordHistoryUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import streamcompass.architecture.presentation.generated.resources.Res
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_about
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_recommended
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_review

class DetailViewModel(
    private val tmdbId: Int,
    private val streamType: StreamType,
    private val recordHistory: Boolean,
    private val getStreamDetailUseCase: GetStreamDetailUseCase,
    private val recordHistoryUseCase: RecordHistoryUseCase,
    getRecommendationsUseCase: GetRecommendationsUseCase,
    getReviewsUseCase: GetReviewsUseCase,
) : ViewModel() {

    val stateFlow: StateFlow<State>

    val recommendationsFlow: Flow<PagingData<Stream>> =
        getRecommendationsUseCase(tmdbId = tmdbId, streamType = streamType).cachedIn(viewModelScope)

    val reviewsFlow: Flow<PagingData<Review>> =
        getReviewsUseCase(tmdbId = tmdbId, streamType = streamType).cachedIn(viewModelScope)

    private val eventChannel: Channel<Event>

    init {
        eventChannel = Channel()
        stateFlow = eventChannel.receiveAsFlow()
            .onStart {
                emit(Event.Initialize)
            }
            .runningFold(
                initial = State.Loading,
                operation = ::handleEvent
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = State.Loading
            )
    }

    fun onTabSelected(tab: Tab) {
        viewModelScope.launch {
            eventChannel.send(Event.SelectTab(tab = tab))
        }
    }

    private suspend fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> {
            val streamDetail = getStreamDetailUseCase(
                tmdbId = tmdbId,
                locale = DEFAULT_LOCALE,
                streamType = streamType,
            )
            if (recordHistory) {
                recordHistoryUseCase(stream = streamDetail.toStream())
            }
            State.Succeed(streamDetail = streamDetail)
        }

        is Event.SelectTab -> when (current) {
            is State.Succeed -> current.copy(selectedTab = event.tab)
            State.Loading -> current
        }
    }

    private fun StreamDetail.toStream(): Stream = when (this) {
        is MovieStreamDetail -> MovieStream(
            tmdbId = tmdbId,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = popularity,
            originalLanguage = originalLanguage,
            originalTitle = originalTitle,
        )

        is TvStreamDetail -> TvStream(
            tmdbId = tmdbId,
            name = name,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            firstAirDate = firstAirDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = popularity,
            originalLanguage = originalLanguage,
            originalName = originalName,
        )
    }

    sealed class Tab {
        abstract val label: StringResource

        data object About : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_about
        }

        data object Recommended : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_recommended
        }

        data object Review : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_review
        }

        companion object {
            fun values(): List<Tab> = listOf(About, Recommended, Review)
        }
    }

    sealed interface Event {
        data object Initialize : Event
        data class SelectTab(val tab: Tab) : Event
    }

    sealed interface State {
        data object Loading : State
        data class Succeed(
            val streamDetail: StreamDetail,
            val selectedTab: Tab = Tab.About,
        ) : State
    }

    private companion object {
        const val DEFAULT_LOCALE = "en-US"
    }
}
