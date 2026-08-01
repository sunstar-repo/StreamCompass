package com.sunstar.streamcompass.data.repository

import com.sunstar.streamcompass.data.datasource.tmdb.TmdbDataSource
import com.sunstar.streamcompass.data.mapper.toStream
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamListType
import com.sunstar.streamcompass.domain.repository.StreamRepository

internal class StreamRepositoryImpl(
    private val tmdbDataSource: TmdbDataSource,
) : StreamRepository {

    override suspend fun getStreams(
        type: StreamListType,
        page: Int,
    ): List<Stream> {
        val response =
            when (type) {
                StreamListType.NowPlaying -> tmdbDataSource.getNowPlaying(page = page)
                StreamListType.Popular -> tmdbDataSource.getPopular(page = page)
                StreamListType.TopRated -> tmdbDataSource.getTopRated(page = page)
                StreamListType.Upcoming -> tmdbDataSource.getUpcoming(page = page)
            }
        return response.results.map { it.toStream() }
    }

    override suspend fun getStream(streamId: Int): Stream =
        tmdbDataSource.getMovieDetail(tmdbId = streamId).toStream()
}
