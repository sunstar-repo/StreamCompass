package com.sunstar.streamcompass.presentation.di

import com.sunstar.streamcompass.presentation.StreamCompassAppViewModel
import com.sunstar.streamcompass.presentation.detail.DetailViewModel
import com.sunstar.streamcompass.presentation.home.HomeViewModel
import com.sunstar.streamcompass.presentation.movie.MovieViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel { HomeViewModel(get()) }
        viewModel { MovieViewModel(get()) }
        viewModel { StreamCompassAppViewModel(get()) }
        viewModel { (tmdbId: Int) ->
            DetailViewModel(
                tmdbId = tmdbId,
                getStreamDetailUseCase = get()
            )
        }
    }
