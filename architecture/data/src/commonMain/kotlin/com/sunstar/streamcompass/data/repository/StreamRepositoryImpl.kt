package com.sunstar.streamcompass.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sunstar.streamcompass.data.converter.toDeeplink
import com.sunstar.streamcompass.data.converter.toEntities
import com.sunstar.streamcompass.data.converter.toEntity
import com.sunstar.streamcompass.data.converter.toFirestoreDeeplinkDtos
import com.sunstar.streamcompass.data.datasource.firestore.FirestoreDataSource
import com.sunstar.streamcompass.data.datasource.local.LocalDataSource
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaConstants
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTrendingItemDto
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbSuggestionPagingSource
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow
import java.util.Locale

internal class StreamRepositoryImpl(
    private val tmdbDataSource: TmdbDataSource,
    private val saDataSource: SaDataSource,
    private val localDataSource: LocalDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val summaryMapper: Mapper<TmdbMovieSummaryDto, MovieStream>,
    private val trendingMapper: Mapper<TmdbTrendingItemDto, Stream>,
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

    override suspend fun getTrendingStream(): List<Stream> {
        val response = tmdbDataSource.getTrendingAllDay(page = 1)
        return response.results
            .filter { it.mediaType == TmdbConstants.MEDIA_TYPE_MOVIE || it.mediaType == TmdbConstants.MEDIA_TYPE_TV }
            .map { trendingMapper.map(source = it) }
            .take(TmdbConstants.PAGE_SIZE)
    }

    override suspend fun getStreamDetail(tmdbId: Int, locale: String): MovieStreamDetail {
        val detailEntity = localDataSource.getMovieDetail(tmdbId = tmdbId, locale = locale)
            ?: fetchAndCacheDetail(tmdbId = tmdbId, locale = locale)
        val country = locale.split("-").lastOrNull()

        val deeplinks = if (null != country) {
            localDataSource.getDeeplinks(
                tmdbId = tmdbId,
                streamType = SaConstants.PATH_MOVIE,
                country = country
            ).map {
                deeplinkEntityMapper.map(source = it)
            }.ifEmpty {
                fetchAndCacheDeeplinks(tmdbId = tmdbId, country = country)
            }
        } else listOf()

        return detailEntityMapper.map(source = detailEntity).copy(deeplinks = deeplinks)
    }

    private suspend fun fetchAndCacheDetail(tmdbId: Int, locale: String): LocalMovieDetailEntity {
        val dto = tmdbDataSource.getMovieDetail(tmdbId = tmdbId, language = locale)
        val entity = dto.toEntity(locale = locale)
        localDataSource.upsertMovieDetail(entity = entity)
        return entity
    }

    private suspend fun fetchAndCacheDeeplinks(tmdbId: Int, country: String): List<Deeplink> {
        val cachedDeeplinks =
            firestoreDataSource.getDeeplinks(
                tmdbId = tmdbId,
                streamType = StreamType.Movie,
                country = country
            )
        if (cachedDeeplinks.isNotEmpty()) return cachedDeeplinks

        val dto = saDataSource.getMovie(tmdbId = tmdbId, country = country)
        localDataSource.replaceDeeplinks(
            tmdbId = tmdbId,
            streamType = SaConstants.PATH_MOVIE,
            country = country,
            entities = dto.toEntities(),
        )

        val firestoreDtos = dto.toFirestoreDeeplinkDtos()
        firestoreDataSource.setDeeplinks(
            tmdbId = tmdbId,
            streamType = StreamType.Movie,
            country = country,
            dtos = firestoreDtos
        )

        return firestoreDtos.map { (service, firestoreDto) ->
            firestoreDto.toDeeplink(
                tmdbId = tmdbId,
                streamType = StreamType.Movie,
                country = country,
                service = service
            )
        }
    }
}
