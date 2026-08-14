package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvDetailDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.StreamDetail.TvStreamDetail

internal class TmdbTvDetailMapper : Mapper<TmdbTvDetailDto, TvStreamDetail> {
    override fun map(source: TmdbTvDetailDto): TvStreamDetail =
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
            genres = source.genres.map { it.name },
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
