package com.sunstar.streamcompass.presentation.di

import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.presentation.StreamCompassAppViewModel
import com.sunstar.streamcompass.presentation.allstreams.AllStreamsViewModel
import com.sunstar.streamcompass.presentation.detail.DetailViewModel
import com.sunstar.streamcompass.presentation.home.HomeViewModel
import com.sunstar.streamcompass.presentation.movie.MovieViewModel
import com.sunstar.streamcompass.presentation.search.SearchViewModel
import com.sunstar.streamcompass.presentation.setting.SettingViewModel
import com.sunstar.streamcompass.presentation.tv.TvViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel {
            HomeViewModel(
                removeHistoryUseCase = get(),
                getTrendingStreamUseCase = get(),
                getSuggestionUseCase = get(),
                getHistoryStreamUseCase = get(),
                getWatchlistStreamUseCase = get(),
                addWatchlistUseCase = get(),
                removeWatchlistUseCase = get(),
            )
        }
        viewModel {
            MovieViewModel(
                getSuggestionUseCase = get(),
                getWatchlistStreamUseCase = get(),
                addWatchlistUseCase = get(),
                removeWatchlistUseCase = get(),
            )
        }
        viewModel {
            TvViewModel(
                getSuggestionUseCase = get(),
                getWatchlistStreamUseCase = get(),
                addWatchlistUseCase = get(),
                removeWatchlistUseCase = get(),
            )
        }
        viewModel { SettingViewModel(setThemeModeUseCase = get(), getThemeModeUseCase = get()) }
        viewModel {
            StreamCompassAppViewModel(
                initializeAppUseCase = get(),
                getThemeModeUseCase = get(),
            )
        }
        viewModel { (tmdbId: Int, streamType: StreamType, recordHistory: Boolean) ->
            DetailViewModel(
                tmdbId = tmdbId,
                streamType = streamType,
                recordHistory = recordHistory,
                getStreamDetailUseCase = get(),
                recordHistoryUseCase = get(),
                getRecommendationsUseCase = get(),
                getReviewsUseCase = get(),
                getSeasonsUseCase = get(),
                getEpisodesUseCase = get(),
                addWatchlistUseCase = get(),
                removeWatchlistUseCase = get(),
                isWatchlistedUseCase = get(),
            )
        }
        viewModel { (rowId: String, streamType: StreamType) ->
            AllStreamsViewModel(
                rowId = rowId,
                streamType = streamType,
                getSuggestionUseCase = get(),
                getHistoryStreamUseCase = get(),
                getWatchlistStreamUseCase = get(),
            )
        }
        viewModel {
            SearchViewModel(
                getSearchHistoryUseCase = get(),
                getSearchStreamUseCase = get(),
                recordSearchHistoryUseCase = get(),
            )
        }
    }
