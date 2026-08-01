package com.sunstar.streamcompass.data.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieSummaryDto
import com.sunstar.streamcompass.domain.model.Stream

internal fun TmdbMovieSummaryDto.toStream(): Stream =
    Stream(
        tmdbId = tmdbId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
    )

internal fun TmdbMovieDetailDto.toStream(): Stream =
    Stream(
        tmdbId = tmdbId,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
    )
