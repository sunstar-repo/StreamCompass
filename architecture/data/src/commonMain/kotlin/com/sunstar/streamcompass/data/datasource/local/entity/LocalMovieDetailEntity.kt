package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity

@Entity(tableName = "movie_detail", primaryKeys = ["tmdbId", "locale"])
internal data class LocalMovieDetailEntity(
    val tmdbId: Int,
    val locale: String,
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
    val genres: List<String>,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val homepage: String,
    val imdbId: String,
    val budget: Long,
    val revenue: Long,
    val adult: Boolean,
    val video: Boolean,
    val logo: String,
    val backdrops: List<String>,
    val certification: String,
)
