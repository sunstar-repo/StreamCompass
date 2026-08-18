package com.sunstar.streamcompass.data.datasource.tmdb.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbSeasonSummaryDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Season

internal class TmdbSeasonsPagingSource(
    private val tmdbDataSource: TmdbDataSource,
    private val seasonSummaryMapper: Mapper<TmdbSeasonSummaryDto, Season>,
    private val tmdbId: Int,
    private val locale: String,
) : PagingSource<Int, Season>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Season> =
        try {
            val response = tmdbDataSource.getTvDetail(tmdbId = tmdbId, language = locale)
            LoadResult.Page(
                data = response.seasons.map { seasonSummaryMapper.map(it) }.sortedByDescending { it.seasonNumber },
                prevKey = null,
                nextKey = null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Season>): Int? = null
}
