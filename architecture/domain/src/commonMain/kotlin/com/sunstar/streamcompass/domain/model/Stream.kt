package com.sunstar.streamcompass.domain.model

sealed interface Stream {
    data class MovieStream(
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
    ) : Stream

    data class TvStream(
        val tmdbId: Int,
        val name: String,
        val overview: String,
        val posterPath: String,
        val backdropPath: String,
        val firstAirDate: String,
        val voteAverage: Double,
        val voteCount: Int,
        val popularity: Double,
        val originalLanguage: String,
        val originalName: String,
    ) : Stream
}
