package com.sunstar.streamcompass.domain.repository

import com.sunstar.streamcompass.domain.model.ApiKey

interface InitializeRepository {
    suspend fun initialize(): ApiKey
}
