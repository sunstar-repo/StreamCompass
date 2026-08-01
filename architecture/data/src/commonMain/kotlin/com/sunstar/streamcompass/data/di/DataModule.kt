package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.repository.StreamRepositoryImpl
import com.sunstar.streamcompass.domain.repository.StreamRepository
import org.koin.dsl.module

val dataModule =
    module {
        single { TmdbDataSource() }

        single<StreamRepository> { StreamRepositoryImpl(tmdbDataSource = get()) }
    }
