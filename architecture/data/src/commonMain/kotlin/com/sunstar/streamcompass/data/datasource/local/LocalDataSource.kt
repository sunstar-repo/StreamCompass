package com.sunstar.streamcompass.data.datasource.local

import com.sunstar.streamcompass.data.datasource.local.dao.DeeplinkDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieDetailDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvHistoryDao
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import kotlinx.coroutines.flow.Flow

internal class LocalDataSource(
    private val movieDetailDao: MovieDetailDao,
    private val deeplinkDao: DeeplinkDao,
    private val movieHistoryDao: MovieHistoryDao,
    private val tvHistoryDao: TvHistoryDao,
) {
    suspend fun getMovieDetail(tmdbId: Int, locale: String): LocalMovieDetailEntity? =
        movieDetailDao.get(tmdbId = tmdbId, locale = locale)

    suspend fun upsertMovieDetail(entity: LocalMovieDetailEntity) =
        movieDetailDao.upsert(entity = entity)

    suspend fun getDeeplinks(
        tmdbId: Int,
        streamType: String,
        country: String
    ): List<LocalDeeplinkEntity> =
        deeplinkDao.get(tmdbId = tmdbId, streamType = streamType, country = country)

    suspend fun replaceDeeplinks(
        tmdbId: Int,
        streamType: String,
        country: String,
        entities: List<LocalDeeplinkEntity>,
    ) = deeplinkDao.replace(
        tmdbId = tmdbId,
        streamType = streamType,
        country = country,
        entities = entities
    )

    fun observeMovieHistory(): Flow<List<LocalMovieHistoryEntity>> = movieHistoryDao.observeRecent()

    suspend fun upsertMovieHistory(entity: LocalMovieHistoryEntity) = movieHistoryDao.upsert(entity = entity)

    fun observeTvHistory(): Flow<List<LocalTvHistoryEntity>> = tvHistoryDao.observeRecent()

    suspend fun upsertTvHistory(entity: LocalTvHistoryEntity) = tvHistoryDao.upsert(entity = entity)

    suspend fun deleteMovieHistory(tmdbId: Int) = movieHistoryDao.delete(tmdbId = tmdbId)

    suspend fun deleteTvHistory(tmdbId: Int) = tvHistoryDao.delete(tmdbId = tmdbId)
}
