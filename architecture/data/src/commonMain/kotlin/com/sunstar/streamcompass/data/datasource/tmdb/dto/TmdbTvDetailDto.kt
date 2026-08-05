package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbTvDetailDto(
    @SerialName("id") val tmdbId: Int,
    val name: String = Constants.EMPTY_STRING,
    val originalName: String = Constants.EMPTY_STRING,
    val overview: String = Constants.EMPTY_STRING,
    val posterPath: String = Constants.EMPTY_STRING,
    val backdropPath: String = Constants.EMPTY_STRING,
    val firstAirDate: String = Constants.EMPTY_STRING,
    val lastAirDate: String = Constants.EMPTY_STRING,
    val voteAverage: Double = Constants.UNSET_DOUBLE,
    val voteCount: Int = Constants.UNSET_INT,
    val popularity: Double = Constants.UNSET_DOUBLE,
    val originalLanguage: String = Constants.EMPTY_STRING,
    val genres: List<TmdbGenreDto>,
    val numberOfSeasons: Int = Constants.UNSET_INT,
    val numberOfEpisodes: Int = Constants.UNSET_INT,
    val episodeRunTime: List<Int>,
    val status: String = Constants.EMPTY_STRING,
    val tagline: String = Constants.EMPTY_STRING,
    val homepage: String = Constants.EMPTY_STRING,
    val inProduction: Boolean,
)
