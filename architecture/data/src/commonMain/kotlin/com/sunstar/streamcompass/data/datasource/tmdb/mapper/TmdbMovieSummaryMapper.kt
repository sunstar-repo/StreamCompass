package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream

internal class TmdbMovieSummaryMapper : Mapper<TmdbMovieSummaryDto, Stream> {
    override fun map(source: TmdbMovieSummaryDto): Stream =
        Stream(
            tmdbId = source.tmdbId,
            title = source.title,
            overview = source.overview,
            posterPath = TmdbConstants.posterUrl(source.posterPath),
            backdropPath = source.backdropPath,
            releaseDate = source.releaseDate,
            voteAverage = source.voteAverage,
            voteCount = source.voteCount,
            popularity = source.popularity,
            originalLanguage = source.originalLanguage,
            originalTitle = source.originalTitle,
        )
}
