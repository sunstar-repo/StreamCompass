package com.sunstar.streamcompass.data.datasource.tmdb.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvSummaryDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream.TvStream

internal class TmdbTvSearchPagingSource(
    private val tmdbDataSource: TmdbDataSource,
    private val summaryMapper: Mapper<TmdbTvSummaryDto, TvStream>,
    private val query: String,
) : PagingSource<Int, TvStream>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TvStream> {
        val page = params.key ?: 1
        return try {
            val response = tmdbDataSource.searchTv(query = query, page = page)
            LoadResult.Page(
                data = response.results.map { summaryMapper.map(it) },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= response.totalPages) null else page + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, TvStream>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
}
