package com.sunstar.streamcompass.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sunstar.streamcompass.domain.model.Episode
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Season
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.currentSystemLocale
import com.sunstar.streamcompass.domain.usecase.AddWatchlistUseCase
import com.sunstar.streamcompass.domain.usecase.GetEpisodesUseCase
import com.sunstar.streamcompass.domain.usecase.GetRecommendationsUseCase
import com.sunstar.streamcompass.domain.usecase.GetReviewsUseCase
import com.sunstar.streamcompass.domain.usecase.GetSeasonsUseCase
import com.sunstar.streamcompass.domain.usecase.GetStreamDetailUseCase
import com.sunstar.streamcompass.domain.usecase.IsWatchlistedUseCase
import com.sunstar.streamcompass.domain.usecase.RecordHistoryUseCase
import com.sunstar.streamcompass.domain.usecase.RemoveWatchlistUseCase
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
import streamcompass.architecture.presentation.generated.resources.stream_detail_tab_series

class DetailViewModel(
    private val tmdbId: Int,
    private val streamType: StreamType,
    private val recordHistory: Boolean,
    private val getStreamDetailUseCase: GetStreamDetailUseCase,
    private val recordHistoryUseCase: RecordHistoryUseCase,
    private val getRecommendationsUseCase: GetRecommendationsUseCase,
    private val getReviewsUseCase: GetReviewsUseCase,
    private val getSeasonsUseCase: GetSeasonsUseCase,
    private val getEpisodesUseCase: GetEpisodesUseCase,
    private val addWatchlistUseCase: AddWatchlistUseCase,
    private val removeWatchlistUseCase: RemoveWatchlistUseCase,
    private val isWatchlistedUseCase: IsWatchlistedUseCase,
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
                initial = State.Loading,
                operation = ::handleEvent
            )
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = State.Loading
            )
    }

    fun onTabSelected(tab: Tab) {
        viewModelScope.launch {
            eventChannel.send(Event.SelectTab(tab = tab))
        }
    }

    fun onSeasonSelected(seasonNumber: Int) {
        viewModelScope.launch {
            eventChannel.send(Event.SelectSeason(seasonNumber = seasonNumber))
        }
    }

    fun onWatchlistToggleClick() {
        viewModelScope.launch {
            eventChannel.send(Event.ToggleWatchlist)
        }
    }

    private suspend fun handleEvent(current: State, event: Event): State = when (event) {
        is Event.Initialize -> {
            val streamDetail = getStreamDetailUseCase(
                tmdbId = tmdbId,
                locale = currentSystemLocale().locale,
                streamType = streamType,
            )
            if (recordHistory) {
                recordHistoryUseCase(stream = streamDetail.toStream())
            }
            val latestSeasonNumber = when (streamDetail) {
                is TvStreamDetail -> streamDetail.numberOfSeasons.takeIf { it > 0 }
                    ?: DEFAULT_SEASON_NUMBER

                is MovieStreamDetail -> DEFAULT_SEASON_NUMBER
            }
            State.Succeed(
                streamDetail = streamDetail,
                recommendationsFlow = getRecommendationsUseCase(
                    tmdbId = tmdbId,
                    streamType = streamType
                )
                    .cachedIn(viewModelScope),
                reviewsFlow = getReviewsUseCase(tmdbId = tmdbId, streamType = streamType)
                    .cachedIn(viewModelScope),
                seasonsFlow = getSeasonsUseCase(
                    tmdbId = tmdbId,
                    locale = currentSystemLocale().locale
                )
                    .cachedIn(viewModelScope),
                episodesFlow = episodesFlow(seasonNumber = latestSeasonNumber),
                selectedSeasonNumber = latestSeasonNumber,
                isWatchlisted = isWatchlistedUseCase(tmdbId = tmdbId, streamType = streamType),
            )
        }

        is Event.SelectTab -> when (current) {
            is State.Succeed -> current.copy(selectedTab = event.tab)
            State.Loading -> current
        }

        is Event.SelectSeason -> when (current) {
            is State.Succeed -> current.copy(
                selectedSeasonNumber = event.seasonNumber,
                episodesFlow = episodesFlow(seasonNumber = event.seasonNumber),
            )

            State.Loading -> current
        }

        is Event.ToggleWatchlist -> when (current) {
            is State.Succeed -> {
                val newValue = !current.isWatchlisted
                if (newValue) {
                    addWatchlistUseCase(stream = current.streamDetail.toStream())
                } else {
                    removeWatchlistUseCase(tmdbId = tmdbId, streamType = streamType)
                }
                current.copy(isWatchlisted = newValue)
            }

            State.Loading -> current
        }
    }

    private fun episodesFlow(seasonNumber: Int): Flow<PagingData<Episode>> =
        getEpisodesUseCase(
            tmdbId = tmdbId,
            seasonNumber = seasonNumber,
            locale = currentSystemLocale().locale
        )
            .cachedIn(viewModelScope)

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

        data object Series : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_series
        }

        data object Recommended : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_recommended
        }

        data object Review : Tab() {
            override val label: StringResource = Res.string.stream_detail_tab_review
        }

        companion object {
            fun values(streamType: StreamType): List<Tab> = when (streamType) {
                StreamType.Movie -> listOf(About, Recommended, Review)
                StreamType.Tv -> listOf(About, Series, Recommended, Review)
            }
        }
    }

    sealed interface Event {
        data object Initialize : Event
        data class SelectTab(val tab: Tab) : Event
        data class SelectSeason(val seasonNumber: Int) : Event
        data object ToggleWatchlist : Event
    }

    sealed interface State {
        data object Loading : State
        data class Succeed(
            val streamDetail: StreamDetail,
            val recommendationsFlow: Flow<PagingData<Stream>>,
            val reviewsFlow: Flow<PagingData<Review>>,
            val seasonsFlow: Flow<PagingData<Season>>,
            val episodesFlow: Flow<PagingData<Episode>>,
            val selectedTab: Tab = Tab.About,
            val selectedSeasonNumber: Int = DEFAULT_SEASON_NUMBER,
            val isWatchlisted: Boolean = false,
        ) : State
    }

    private companion object {
        const val DEFAULT_SEASON_NUMBER = 1
    }
}
