package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTrendingItemDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream

internal class TmdbTrendingItemMapper : Mapper<TmdbTrendingItemDto, Stream> {
    override fun map(source: TmdbTrendingItemDto): Stream =
        when (source.mediaType) {
            TmdbConstants.MEDIA_TYPE_MOVIE -> Stream.MovieStream(
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

            else -> Stream.TvStream(
                tmdbId = source.tmdbId,
                name = source.name,
                overview = source.overview,
                posterPath = source.posterPath,
                backdropPath = source.backdropPath,
                firstAirDate = source.firstAirDate,
                voteAverage = source.voteAverage,
                voteCount = source.voteCount,
                popularity = source.popularity,
                originalLanguage = source.originalLanguage,
                originalName = source.originalName,
            )
        }
}
