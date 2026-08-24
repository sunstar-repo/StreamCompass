package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieWatchlistEntity
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream.MovieStream

internal class LocalMovieWatchlistEntityMapper : Mapper<LocalMovieWatchlistEntity, MovieStream> {
    override fun map(source: LocalMovieWatchlistEntity): MovieStream =
        MovieStream(
            tmdbId = source.tmdbId,
            title = source.title,
            overview = source.overview,
            posterPath = source.posterPath,
            backdropPath = source.backdropPath,
            releaseDate = source.releaseDate,
            voteAverage = source.voteAverage,
            voteCount = source.voteCount,
            popularity = source.popularity,
            originalLanguage = source.originalLanguage,
            originalTitle = source.originalTitle,
        )
}
