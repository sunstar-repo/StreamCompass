package com.sunstar.streamcompass.data.datasource.tmdb.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbEpisodeDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Episode

internal class TmdbEpisodesPagingSource(
    private val tmdbDataSource: TmdbDataSource,
    private val episodeMapper: Mapper<TmdbEpisodeDto, Episode>,
    private val tmdbId: Int,
    private val seasonNumber: Int,
    private val locale: String,
) : PagingSource<Int, Episode>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Episode> =
        try {
            val response = tmdbDataSource.getSeasonDetail(
                tmdbId = tmdbId,
                seasonNumber = seasonNumber,
                language = locale,
            )
            LoadResult.Page(
                data = response.episodes.map { episodeMapper.map(it) }
                    .sortedByDescending { it.episodeNumber },
                prevKey = null,
                nextKey = null,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    override fun getRefreshKey(state: PagingState<Int, Episode>): Int? = null
}
