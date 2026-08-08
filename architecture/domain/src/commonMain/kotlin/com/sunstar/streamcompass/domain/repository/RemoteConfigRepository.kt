package com.sunstar.streamcompass.domain.repository

import com.sunstar.streamcompass.domain.model.ApiKey

interface RemoteConfigRepository {
    suspend fun initialize(): ApiKey
}
