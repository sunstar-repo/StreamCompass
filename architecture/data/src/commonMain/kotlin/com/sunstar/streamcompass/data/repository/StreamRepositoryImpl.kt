package com.sunstar.streamcompass.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.data.datasource.tmdb.paging.TmdbSuggestionPagingSource
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.SuggestionType
import com.sunstar.streamcompass.domain.repository.StreamRepository
import kotlinx.coroutines.flow.Flow

internal class StreamRepositoryImpl(
    private val tmdbDataSource: TmdbDataSource,
    private val summaryMapper: Mapper<TmdbMovieSummaryDto, Stream>,
    private val detailMapper: Mapper<TmdbMovieDetailDto, Stream>,
) : StreamRepository {

    override fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<Stream>> =
        Pager(
            config = PagingConfig(pageSize = TmdbConstants.PAGE_SIZE),
            pagingSourceFactory = { TmdbSuggestionPagingSource(tmdbDataSource, summaryMapper, type) },
        ).flow

    override suspend fun getStream(streamId: Int): Stream =
        detailMapper.map(tmdbDataSource.getMovieDetail(tmdbId = streamId))
}
