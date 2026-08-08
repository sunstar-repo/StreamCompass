package com.sunstar.streamcompass.presentation.di

import com.sunstar.streamcompass.presentation.StreamCompassAppViewModel
import com.sunstar.streamcompass.presentation.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule =
    module {
        viewModel { DashboardViewModel(get()) }
        viewModel { StreamCompassAppViewModel(get()) }
    }
