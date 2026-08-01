package com.sunstar.streamcompass.domain.model

data class Stream(
    val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val originalLanguage: String,
    val originalTitle: String,
)
