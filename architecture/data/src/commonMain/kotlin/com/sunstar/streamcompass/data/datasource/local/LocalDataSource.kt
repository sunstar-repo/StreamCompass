package com.sunstar.streamcompass.data.datasource.local

import com.sunstar.streamcompass.data.datasource.local.dao.DeeplinkDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieDetailDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.MovieWatchlistDao
import com.sunstar.streamcompass.data.datasource.local.dao.SearchHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvDetailDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvHistoryDao
import com.sunstar.streamcompass.data.datasource.local.dao.TvWatchlistDao
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieWatchlistEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalSearchHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvWatchlistEntity
import kotlinx.coroutines.flow.Flow

internal class LocalDataSource(
    private val movieDetailDao: MovieDetailDao,
    private val tvDetailDao: TvDetailDao,
    private val deeplinkDao: DeeplinkDao,
    private val movieHistoryDao: MovieHistoryDao,
    private val tvHistoryDao: TvHistoryDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val movieWatchlistDao: MovieWatchlistDao,
    private val tvWatchlistDao: TvWatchlistDao,
) {
    suspend fun getMovieDetail(tmdbId: Int, locale: String): LocalMovieDetailEntity? =
        movieDetailDao.get(tmdbId = tmdbId, locale = locale)

    suspend fun upsertMovieDetail(entity: LocalMovieDetailEntity) =
        movieDetailDao.upsert(entity = entity)

    suspend fun getTvDetail(tmdbId: Int, locale: String): LocalTvDetailEntity? =
        tvDetailDao.get(tmdbId = tmdbId, locale = locale)

    suspend fun upsertTvDetail(entity: LocalTvDetailEntity) =
        tvDetailDao.upsert(entity = entity)

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

    suspend fun upsertMovieHistory(entity: LocalMovieHistoryEntity) =
        movieHistoryDao.upsert(entity = entity)

    fun observeTvHistory(): Flow<List<LocalTvHistoryEntity>> = tvHistoryDao.observeRecent()

    suspend fun upsertTvHistory(entity: LocalTvHistoryEntity) = tvHistoryDao.upsert(entity = entity)

    suspend fun deleteMovieHistory(tmdbId: Int) = movieHistoryDao.delete(tmdbId = tmdbId)

    suspend fun deleteTvHistory(tmdbId: Int) = tvHistoryDao.delete(tmdbId = tmdbId)

    fun observeSearchHistory(): Flow<List<LocalSearchHistoryEntity>> = searchHistoryDao.observeRecent()

    suspend fun upsertSearchHistory(entity: LocalSearchHistoryEntity) =
        searchHistoryDao.upsert(entity = entity)

    fun observeMovieWatchlist(): Flow<List<LocalMovieWatchlistEntity>> = movieWatchlistDao.observeAll()

    fun observeTvWatchlist(): Flow<List<LocalTvWatchlistEntity>> = tvWatchlistDao.observeAll()

    suspend fun upsertMovieWatchlist(entity: LocalMovieWatchlistEntity) =
        movieWatchlistDao.upsert(entity = entity)

    suspend fun upsertTvWatchlist(entity: LocalTvWatchlistEntity) = tvWatchlistDao.upsert(entity = entity)

    suspend fun deleteMovieWatchlist(tmdbId: Int) = movieWatchlistDao.delete(tmdbId = tmdbId)

    suspend fun deleteTvWatchlist(tmdbId: Int) = tvWatchlistDao.delete(tmdbId = tmdbId)

    suspend fun isMovieWatchlisted(tmdbId: Int): Boolean = movieWatchlistDao.exists(tmdbId = tmdbId)

    suspend fun isTvWatchlisted(tmdbId: Int): Boolean = tvWatchlistDao.exists(tmdbId = tmdbId)
}
