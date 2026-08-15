package com.sunstar.streamcompass.data.datasource.tmdb.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream.MovieStream

internal class TmdbMovieRecommendationsPagingSource(
    private val tmdbDataSource: TmdbDataSource,
    private val summaryMapper: Mapper<TmdbMovieSummaryDto, MovieStream>,
    private val tmdbId: Int,
) : PagingSource<Int, MovieStream>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieStream> {
        val page = params.key ?: 1
        return try {
            val response = tmdbDataSource.getMovieRecommendations(tmdbId = tmdbId, page = page)
            LoadResult.Page(
                data = response.results.map { summaryMapper.map(it) },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieStream>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}
