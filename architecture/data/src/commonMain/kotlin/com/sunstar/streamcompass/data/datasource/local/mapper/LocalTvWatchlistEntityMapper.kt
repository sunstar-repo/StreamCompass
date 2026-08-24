package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvWatchlistEntity
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Stream.TvStream

internal class LocalTvWatchlistEntityMapper : Mapper<LocalTvWatchlistEntity, TvStream> {
    override fun map(source: LocalTvWatchlistEntity): TvStream =
        TvStream(
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
