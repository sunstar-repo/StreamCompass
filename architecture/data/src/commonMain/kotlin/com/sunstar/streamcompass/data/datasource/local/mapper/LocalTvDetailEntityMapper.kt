package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail

internal class LocalTvDetailEntityMapper : Mapper<LocalTvDetailEntity, TvStreamDetail> {
    override fun map(source: LocalTvDetailEntity): TvStreamDetail =
        TvStreamDetail(
            tmdbId = source.tmdbId,
            name = source.name,
            overview = source.overview,
            posterPath = source.posterPath,
            backdropPath = source.backdropPath,
            firstAirDate = source.firstAirDate,
            lastAirDate = source.lastAirDate,
            voteAverage = source.voteAverage,
            voteCount = source.voteCount,
            popularity = source.popularity,
            originalLanguage = source.originalLanguage,
            originalName = source.originalName,
            genres = source.genres,
            numberOfSeasons = source.numberOfSeasons,
            numberOfEpisodes = source.numberOfEpisodes,
            episodeRunTime = source.episodeRunTime,
            status = source.status,
            tagline = source.tagline,
            homepage = source.homepage,
            inProduction = source.inProduction,
            deeplinks = emptyList(),
        )
}
