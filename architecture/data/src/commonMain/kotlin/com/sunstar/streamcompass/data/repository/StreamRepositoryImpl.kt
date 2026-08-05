package com.sunstar.streamcompass.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaDataSource
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaShowDto
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
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
    private val summaryMapper: Mapper<TmdbMovieSummaryDto, MovieStream>,
    private val detailMapper: Mapper<TmdbMovieDetailDto, MovieStreamDetail>,
    private val saDataSource: SaDataSource,
    private val saShowMapper: Mapper<SaShowDto, List<Deeplink>>,
) : StreamRepository {

    override fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<MovieStream>> =
        Pager(
            config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { TmdbSuggestionPagingSource(tmdbDataSource, summaryMapper, type) },
        ).flow

    override suspend fun getStreamDetail(streamId: Int): MovieStreamDetail {
        val detail = detailMapper.map(tmdbDataSource.getMovieDetail(tmdbId = streamId))
        val deeplinks = saShowMapper.map(saDataSource.getMovie(tmdbId = streamId))
        return detail.copy(deeplinks = deeplinks)
    }
}
