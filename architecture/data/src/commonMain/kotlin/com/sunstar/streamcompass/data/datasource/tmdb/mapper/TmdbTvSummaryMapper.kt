package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvSummaryDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream.TvStream

internal class TmdbTvSummaryMapper : Mapper<TmdbTvSummaryDto, TvStream> {
    override fun map(source: TmdbTvSummaryDto): TvStream =
        TvStream(
            tmdbId = source.tmdbId,
            name = source.name,
            overview = source.overview,
            posterPath = TmdbConstants.posterUrl(source.posterPath),
            backdropPath = source.backdropPath,
            firstAirDate = source.firstAirDate,
            voteAverage = source.voteAverage,
            voteCount = source.voteCount,
            popularity = source.popularity,
            originalLanguage = source.originalLanguage,
            originalName = source.originalName,
        )
}
