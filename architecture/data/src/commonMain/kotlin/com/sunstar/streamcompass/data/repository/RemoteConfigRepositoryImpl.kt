package com.sunstar.streamcompass.data.repository

import com.sunstar.streamcompass.data.datasource.remoteconfig.RemoteConfigDataSource
import com.sunstar.streamcompass.domain.repository.RemoteConfigRepository

internal class RemoteConfigRepositoryImpl(
    private val remoteConfigDataSource: RemoteConfigDataSource,
) : RemoteConfigRepository {
    override suspend fun initialize() = remoteConfigDataSource.fetchAndActivate()
}
