package com.sunstar.streamcompass.data.repository

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.firestore.FirestoreDataSource
import com.sunstar.streamcompass.domain.model.ApiKey
import com.sunstar.streamcompass.domain.repository.InitializeRepository

internal class InitializeRepositoryImpl(
    private val firestoreDataSource: FirestoreDataSource,
) : InitializeRepository {
    override suspend fun initialize(): ApiKey =
        try {
            val dto = firestoreDataSource.getApiKey()
            ApiKey(tmdbKey = dto.tmdbApiKey, saKey = dto.saApiKey)
        } catch (throwable: Throwable) {
            ApiKey(tmdbKey = Constants.EMPTY_STRING, saKey = Constants.EMPTY_STRING)
        }
}
