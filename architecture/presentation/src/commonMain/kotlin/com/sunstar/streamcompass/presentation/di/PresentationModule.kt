package com.sunstar.streamcompass.presentation.di

import com.sunstar.streamcompass.presentation.StreamCompassAppViewModel
import com.sunstar.streamcompass.presentation.detail.StreamDetailViewModel
import com.sunstar.streamcompass.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel { HomeViewModel(get()) }
        viewModel { StreamCompassAppViewModel(get()) }
        viewModel { (tmdbId: Int) ->
            StreamDetailViewModel(
                tmdbId = tmdbId,
                getStreamDetailUseCase = get()
            )
        }
    }
