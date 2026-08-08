package com.sunstar.streamcompass.data.di

import com.sunstar.streamcompass.data.datasource.remoteconfig.RemoteConfigDataSource
import com.sunstar.streamcompass.data.repository.RemoteConfigRepositoryImpl
import com.sunstar.streamcompass.domain.repository.RemoteConfigRepository
import com.sunstar.streamcompass.domain.usecase.InitializeAppUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val REMOTE_CONFIG_DATA_SOURCE = named("remoteConfigDataSource")
private val REMOTE_CONFIG_REPOSITORY = named("remoteConfigRepository")

val remoteConfigModule =
    module {
        single(REMOTE_CONFIG_DATA_SOURCE) { RemoteConfigDataSource() }
        single<RemoteConfigRepository>(REMOTE_CONFIG_REPOSITORY) {
            RemoteConfigRepositoryImpl(remoteConfigDataSource = get(qualifier = REMOTE_CONFIG_DATA_SOURCE))
        }
        single { InitializeAppUseCase(remoteConfigRepository = get(qualifier = REMOTE_CONFIG_REPOSITORY)) }
    }
