package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.StreamDetail.MovieStreamDetail

internal class LocalMovieDetailEntityMapper : Mapper<LocalMovieDetailEntity, MovieStreamDetail> {
    override fun map(source: LocalMovieDetailEntity): MovieStreamDetail =
        MovieStreamDetail(
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
            genres = source.genres,
            runtime = source.runtime,
            status = source.status,
            tagline = source.tagline,
            homepage = source.homepage,
            imdbId = source.imdbId,
            budget = source.budget,
            revenue = source.revenue,
            adult = source.adult,
            video = source.video,
            logo = source.logo,
            backdrops = source.backdrops,
            certification = source.certification,
            deeplinks = emptyList(),
        )
}
