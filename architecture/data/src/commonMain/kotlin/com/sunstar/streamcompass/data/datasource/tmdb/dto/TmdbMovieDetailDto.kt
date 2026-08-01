package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieDetailDto(
    @SerialName("id") val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val genres: List<TmdbGenreDto>,
    val runtime: Int?,
    val status: String,
    val tagline: String?,
    val originalLanguage: String,
    val originalTitle: String,
    val homepage: String?,
    val imdbId: String?,
    val budget: Long,
    val revenue: Long,
    val adult: Boolean,
    val video: Boolean,
)
