package com.sunstar.streamcompass.data.datasource.remoteconfig

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.domain.model.ApiKey
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig

internal class RemoteConfigDataSource {
    suspend fun fetchAndActivate(): ApiKey =
        try {
            val remoteConfig = Firebase.remoteConfig

            remoteConfig.fetchAndActivate()

            ApiKey(
                tmdbKey = remoteConfig.getValue(RemoteConfigConstants.TMDB_API_KEY).asString(),
                saKey = remoteConfig.getValue(RemoteConfigConstants.SA_API_KEY).asString(),
            )
        } catch (throwable: Throwable) {
            ApiKey(tmdbKey = Constants.EMPTY_STRING, saKey = Constants.EMPTY_STRING)
        }
}
