package com.sunstar.streamcompass.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sunstar.streamcompass.data.converter.toEntities
import com.sunstar.streamcompass.data.converter.toEntity
import com.sunstar.streamcompass.data.datasource.local.LocalDataSource
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaConstants
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbSuggestionPagingSource
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

internal class StreamRepositoryImpl(
    private val tmdbDataSource: TmdbDataSource,
    private val saDataSource: SaDataSource,
    private val localDataSource: LocalDataSource,
    private val summaryMapper: Mapper<TmdbMovieSummaryDto, MovieStream>,
    private val detailEntityMapper: Mapper<LocalMovieDetailEntity, MovieStreamDetail>,
    private val deeplinkEntityMapper: Mapper<LocalDeeplinkEntity, Deeplink>,
) : StreamRepository {

    override fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<MovieStream>> =
        Pager(
            config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
            pagingSourceFactory = {
                TmdbSuggestionPagingSource(
                    tmdbDataSource = tmdbDataSource,
                    summaryMapper = summaryMapper,
                    type = type,
                )
            },
        ).flow

    override suspend fun getStreamDetail(tmdbId: Int, locale: String): MovieStreamDetail {
        val detailEntity =
            localDataSource.getMovieDetail(tmdbId = tmdbId, locale = locale)
                ?: fetchAndCacheDetail(tmdbId = tmdbId, locale = locale)
        val deeplinks =
            localDataSource.getDeeplinks(
                tmdbId = tmdbId,
                streamType = SaConstants.PATH_MOVIE,
                locale = locale
            )
                .map { deeplinkEntityMapper.map(source = it) }
                .ifEmpty { fetchAndCacheDeeplinks(tmdbId = tmdbId, locale = locale) }

        return detailEntityMapper.map(source = detailEntity).copy(deeplinks = deeplinks)
    }

    private suspend fun fetchAndCacheDetail(tmdbId: Int, locale: String): LocalMovieDetailEntity {
        val dto = tmdbDataSource.getMovieDetail(tmdbId = tmdbId, language = locale)
        val entity = dto.toEntity(locale = locale)
        localDataSource.upsertMovieDetail(entity = entity)
        return entity
    }

    private suspend fun fetchAndCacheDeeplinks(tmdbId: Int, locale: String): List<Deeplink> {
        val dto = saDataSource.getMovie(tmdbId = tmdbId, country = locale)
        val entities = dto.toEntities()
        localDataSource.replaceDeeplinks(
            tmdbId = tmdbId,
            streamType = SaConstants.PATH_MOVIE,
            locale = locale,
            entities = entities,
        )
        return entities.map { deeplinkEntityMapper.map(source = it) }
    }
}
