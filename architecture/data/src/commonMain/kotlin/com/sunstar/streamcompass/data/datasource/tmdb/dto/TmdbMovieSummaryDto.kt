package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbMovieSummaryDto(
    @SerialName("id") val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val genreIds: List<Int>,
    val originalLanguage: String,
    val originalTitle: String,
    val adult: Boolean,
    val video: Boolean,
)
