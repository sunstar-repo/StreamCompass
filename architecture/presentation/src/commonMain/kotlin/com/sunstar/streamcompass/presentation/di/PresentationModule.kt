package com.sunstar.streamcompass.presentation.di

import com.sunstar.streamcompass.presentation.StreamCompassAppViewModel
import com.sunstar.streamcompass.presentation.detail.DetailViewModel
import com.sunstar.streamcompass.presentation.home.HomeViewModel
import com.sunstar.streamcompass.presentation.movie.MovieViewModel
import com.sunstar.streamcompass.presentation.setting.SettingViewModel
import com.sunstar.streamcompass.presentation.tv.TvViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel {
            HomeViewModel(
                getTrendingStreamUseCase = get(),
                getMovieHistoryStreamUseCase = get(),
                getTvHistoryStreamUseCase = get(),
                removeMovieHistoryUseCase = get(),
                removeTvHistoryUseCase = get(),
            )
        }
        viewModel { MovieViewModel(getSuggestionUseCase = get()) }
        viewModel { TvViewModel(getTvSuggestionUseCase = get()) }
        viewModel { SettingViewModel(setThemeModeUseCase = get(), getThemeModeUseCase = get()) }
        viewModel {
            StreamCompassAppViewModel(
                initializeAppUseCase = get(),
                getThemeModeUseCase = get(),
            )
        }
        viewModel { (tmdbId: Int) ->
            DetailViewModel(
                tmdbId = tmdbId,
                getStreamDetailUseCase = get(),
                recordMovieHistoryUseCase = get(),
            )
        }
    }
