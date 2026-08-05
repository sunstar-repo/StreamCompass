package com.sunstar.streamcompass.data.datasource.local

import com.sunstar.streamcompass.data.datasource.local.dao.DeeplinkDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieDetailDao
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity

internal class LocalDataSource(
    private val movieDetailDao: MovieDetailDao,
    private val deeplinkDao: DeeplinkDao,
) {
    suspend fun getMovieDetail(tmdbId: Int, locale: String): LocalMovieDetailEntity? =
        movieDetailDao.get(tmdbId = tmdbId, locale = locale)

    suspend fun upsertMovieDetail(entity: LocalMovieDetailEntity) =
        movieDetailDao.upsert(entity = entity)

    suspend fun getDeeplinks(
        tmdbId: Int,
        streamType: String,
        locale: String
    ): List<LocalDeeplinkEntity> =
        deeplinkDao.get(tmdbId = tmdbId, streamType = streamType, locale = locale)

    suspend fun replaceDeeplinks(
        tmdbId: Int,
        streamType: String,
        locale: String,
        entities: List<LocalDeeplinkEntity>,
    ) = deeplinkDao.replace(
        tmdbId = tmdbId,
        streamType = streamType,
        locale = locale,
        entities = entities
    )
}
