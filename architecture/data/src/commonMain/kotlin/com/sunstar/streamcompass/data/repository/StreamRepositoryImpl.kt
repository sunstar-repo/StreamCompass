package com.sunstar.streamcompass.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sunstar.streamcompass.data.converter.toDeeplink
import com.sunstar.streamcompass.data.converter.toEntities
import com.sunstar.streamcompass.data.converter.toEntity
import com.sunstar.streamcompass.data.converter.toFirestoreDeeplinkDtos
import com.sunstar.streamcompass.data.converter.toWatchlistEntity
import com.sunstar.streamcompass.data.datasource.firestore.FirestoreDataSource
import com.sunstar.streamcompass.data.datasource.local.LocalDataSource
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieWatchlistEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalSearchHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvWatchlistEntity
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbEpisodeDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbReviewDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbSeasonSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTrendingItemDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbEpisodesPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbMovieRecommendationsPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbMovieReviewsPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbSearchPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbSeasonsPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbSuggestionPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbTvRecommendationsPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbTvReviewsPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbTvSearchPagingSource
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbTvSuggestionPagingSource
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Episode
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Season
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.model.currentSystemLocale
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class StreamRepositoryImpl(
    private val tmdbDataSource: TmdbDataSource,
    private val saDataSource: SaDataSource,
    private val localDataSource: LocalDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val movieSummaryMapper: Mapper<TmdbMovieSummaryDto, MovieStream>,
    private val tvSummaryMapper: Mapper<TmdbTvSummaryDto, TvStream>,
    private val trendingMapper: Mapper<TmdbTrendingItemDto, Stream>,
    private val detailEntityMapper: Mapper<LocalMovieDetailEntity, MovieStreamDetail>,
    private val tvDetailEntityMapper: Mapper<LocalTvDetailEntity, TvStreamDetail>,
    private val deeplinkEntityMapper: Mapper<LocalDeeplinkEntity, Deeplink>,
    private val movieHistoryEntityMapper: Mapper<LocalMovieHistoryEntity, MovieStream>,
    private val tvHistoryEntityMapper: Mapper<LocalTvHistoryEntity, TvStream>,
    private val movieWatchlistEntityMapper: Mapper<LocalMovieWatchlistEntity, MovieStream>,
    private val tvWatchlistEntityMapper: Mapper<LocalTvWatchlistEntity, TvStream>,
    private val reviewMapper: Mapper<TmdbReviewDto, Review>,
    private val seasonSummaryMapper: Mapper<TmdbSeasonSummaryDto, Season>,
    private val episodeMapper: Mapper<TmdbEpisodeDto, Episode>,
) : StreamRepository {

    override fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<Stream>> =
        when (type) {
            is SuggestionType.Movie -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbSuggestionPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        summaryMapper = movieSummaryMapper,
                        type = type,
                    )
                },
            ).flow.map { pagingData -> pagingData.map { it } }

            is SuggestionType.Tv -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbTvSuggestionPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        summaryMapper = tvSummaryMapper,
                        type = type,
                    )
                },
            ).flow.map { pagingData -> pagingData.map { it } }
        }

    override suspend fun getTrendingStream(): List<Stream> {
        val response = tmdbDataSource.getTrendingAllDay(page = 1)
        return response.results
            .filter {
                it.mediaType == TmdbConstants.MEDIA_TYPE_MOVIE || it.mediaType == TmdbConstants.MEDIA_TYPE_TV
            }.map {
                trendingMapper.map(source = it)
            }.take(TmdbConstants.PAGE_SIZE)
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun recordHistory(stream: Stream) {
        val visitedAt = Clock.System.now().toEpochMilliseconds()
        when (stream) {
            is MovieStream -> localDataSource.upsertMovieHistory(entity = stream.toEntity(visitedAt = visitedAt))
            is TvStream -> localDataSource.upsertTvHistory(entity = stream.toEntity(visitedAt = visitedAt))
        }
    }

    override fun getHistoryStreamFlow(streamType: StreamType): Flow<List<Stream>> =
        when (streamType) {
            StreamType.Movie -> localDataSource.observeMovieHistory().map { entities ->
                entities.map { movieHistoryEntityMapper.map(source = it) }
            }

            StreamType.Tv -> localDataSource.observeTvHistory().map { entities ->
                entities.map { tvHistoryEntityMapper.map(source = it) }
            }
        }

    override suspend fun removeHistory(tmdbId: Int, streamType: StreamType) =
        when (streamType) {
            StreamType.Movie -> localDataSource.deleteMovieHistory(tmdbId = tmdbId)
            StreamType.Tv -> localDataSource.deleteTvHistory(tmdbId = tmdbId)
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun addWatchlist(stream: Stream) {
        val addedAt = Clock.System.now().toEpochMilliseconds()
        when (stream) {
            is MovieStream -> localDataSource.upsertMovieWatchlist(
                entity = stream.toWatchlistEntity(
                    addedAt = addedAt
                )
            )

            is TvStream -> localDataSource.upsertTvWatchlist(
                entity = stream.toWatchlistEntity(
                    addedAt = addedAt
                )
            )
        }
    }

    override fun getWatchlistStreamFlow(streamType: StreamType): Flow<List<Stream>> =
        when (streamType) {
            StreamType.Movie -> localDataSource.observeMovieWatchlist().map { entities ->
                entities.map { movieWatchlistEntityMapper.map(source = it) }
            }

            StreamType.Tv -> localDataSource.observeTvWatchlist().map { entities ->
                entities.map { tvWatchlistEntityMapper.map(source = it) }
            }
        }

    override suspend fun removeWatchlist(tmdbId: Int, streamType: StreamType) =
        when (streamType) {
            StreamType.Movie -> localDataSource.deleteMovieWatchlist(tmdbId = tmdbId)
            StreamType.Tv -> localDataSource.deleteTvWatchlist(tmdbId = tmdbId)
        }

    override suspend fun isWatchlisted(tmdbId: Int, streamType: StreamType): Boolean =
        when (streamType) {
            StreamType.Movie -> localDataSource.isMovieWatchlisted(tmdbId = tmdbId)
            StreamType.Tv -> localDataSource.isTvWatchlisted(tmdbId = tmdbId)
        }

    override fun getSearchStreamFlow(
        query: String,
        streamType: StreamType
    ): Flow<PagingData<Stream>> =
        when (streamType) {
            StreamType.Movie -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbSearchPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        summaryMapper = movieSummaryMapper,
                        query = query,
                    )
                },
            ).flow.map { pagingData -> pagingData.map { it } }

            StreamType.Tv -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbTvSearchPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        summaryMapper = tvSummaryMapper,
                        query = query,
                    )
                },
            ).flow.map { pagingData -> pagingData.map { it } }
        }

    @OptIn(ExperimentalTime::class)
    override suspend fun recordSearchHistory(query: String) {
        val searchedAt = Clock.System.now().toEpochMilliseconds()
        localDataSource.upsertSearchHistory(
            entity = LocalSearchHistoryEntity(query = query, searchedAt = searchedAt)
        )
    }

    override fun getSearchHistoryFlow(): Flow<List<String>> =
        localDataSource.observeSearchHistory().map { entities -> entities.map { it.query } }

    override fun getRecommendationsStreamFlow(
        tmdbId: Int,
        streamType: StreamType
    ): Flow<PagingData<Stream>> =
        when (streamType) {
            StreamType.Movie -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbMovieRecommendationsPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        summaryMapper = movieSummaryMapper,
                        tmdbId = tmdbId,
                    )
                },
            ).flow.map { pagingData -> pagingData.map { it } }

            StreamType.Tv -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbTvRecommendationsPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        summaryMapper = tvSummaryMapper,
                        tmdbId = tmdbId,
                    )
                },
            ).flow.map { pagingData -> pagingData.map { it } }
        }

    override fun getReviewsStreamFlow(
        tmdbId: Int,
        streamType: StreamType
    ): Flow<PagingData<Review>> =
        when (streamType) {
            StreamType.Movie -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbMovieReviewsPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        reviewMapper = reviewMapper,
                        tmdbId = tmdbId,
                    )
                },
            ).flow

            StreamType.Tv -> Pager(
                config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
                pagingSourceFactory = {
                    TmdbTvReviewsPagingSource(
                        tmdbDataSource = tmdbDataSource,
                        reviewMapper = reviewMapper,
                        tmdbId = tmdbId,
                    )
                },
            ).flow
        }

    // 항상 최신 정보를 불러온다 — Room/Firestore 어디에도 캐싱하지 않고 tmdbDataSource를 직접 호출한다.
    override fun getSeasonsStreamFlow(tmdbId: Int, locale: String): Flow<PagingData<Season>> =
        Pager(
            config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
            pagingSourceFactory = {
                TmdbSeasonsPagingSource(
                    tmdbDataSource = tmdbDataSource,
                    seasonSummaryMapper = seasonSummaryMapper,
                    tmdbId = tmdbId,
                    locale = locale,
                )
            },
        ).flow

    override fun getEpisodesStreamFlow(
        tmdbId: Int,
        seasonNumber: Int,
        locale: String
    ): Flow<PagingData<Episode>> =
        Pager(
            config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
            pagingSourceFactory = {
                TmdbEpisodesPagingSource(
                    tmdbDataSource = tmdbDataSource,
                    episodeMapper = episodeMapper,
                    tmdbId = tmdbId,
                    seasonNumber = seasonNumber,
                    locale = locale,
                )
            },
        ).flow

    override suspend fun getStreamDetail(
        tmdbId: Int,
        locale: String,
        streamType: StreamType
    ): StreamDetail {
        val deeplinks = getDeeplinks(tmdbId = tmdbId, streamType = streamType)
        return when (streamType) {
            StreamType.Movie -> {
                val detailEntity = localDataSource.getMovieDetail(tmdbId = tmdbId, locale = locale)
                    ?: fetchAndCacheMovieDetail(tmdbId = tmdbId, locale = locale)
                detailEntityMapper.map(source = detailEntity).copy(deeplinks = deeplinks)
            }

            StreamType.Tv -> {
                val detailEntity = localDataSource.getTvDetail(tmdbId = tmdbId, locale = locale)
                    ?: fetchAndCacheTvDetail(tmdbId = tmdbId, locale = locale)
                tvDetailEntityMapper.map(source = detailEntity).copy(deeplinks = deeplinks)
            }
        }
    }

    private suspend fun fetchAndCacheMovieDetail(
        tmdbId: Int,
        locale: String
    ): LocalMovieDetailEntity {
        val dto = tmdbDataSource.getMovieDetail(tmdbId = tmdbId, language = locale)
        val entity = dto.toEntity(locale = locale)
        localDataSource.upsertMovieDetail(entity = entity)
        return entity
    }

    private suspend fun fetchAndCacheTvDetail(tmdbId: Int, locale: String): LocalTvDetailEntity {
        val dto = tmdbDataSource.getTvDetail(tmdbId = tmdbId, language = locale)
        val entity = dto.toEntity(locale = locale)
        localDataSource.upsertTvDetail(entity = entity)
        return entity
    }

    private suspend fun getDeeplinks(
        tmdbId: Int,
        streamType: StreamType
    ): List<Deeplink> {
        val country = currentSystemLocale().country

        return localDataSource.getDeeplinks(
            tmdbId = tmdbId,
            streamType = streamType.rawValue,
            country = country
        ).map {
            deeplinkEntityMapper.map(source = it)
        }.ifEmpty {
            fetchAndCacheDeeplinks(tmdbId = tmdbId, country = country, streamType = streamType)
        }
    }

    private suspend fun fetchAndCacheDeeplinks(
        tmdbId: Int,
        country: String,
        streamType: StreamType
    ): List<Deeplink> {
        val cachedDeeplinks =
            firestoreDataSource.getDeeplinks(
                tmdbId = tmdbId,
                streamType = streamType,
                country = country
            )
        if (cachedDeeplinks.isNotEmpty()) return cachedDeeplinks

        val dto = when (streamType) {
            StreamType.Movie -> saDataSource.getMovie(tmdbId = tmdbId, country = country)
            StreamType.Tv -> saDataSource.getTvShow(tmdbId = tmdbId, country = country)
        }
        localDataSource.replaceDeeplinks(
            tmdbId = tmdbId,
            streamType = streamType.rawValue,
            country = country,
            entities = dto.toEntities(),
        )

        val firestoreDtos = dto.toFirestoreDeeplinkDtos()
        firestoreDataSource.setDeeplinks(
            tmdbId = tmdbId,
            streamType = streamType,
            country = country,
            dtos = firestoreDtos
        )

        return firestoreDtos.map { (service, firestoreDto) ->
            firestoreDto.toDeeplink(
                tmdbId = tmdbId,
                streamType = streamType,
                country = country,
                service = service
            )
        }
    }
}
