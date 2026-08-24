package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_watchlist")
internal data class LocalMovieWatchlistEntity(
    @PrimaryKey val tmdbId: Int,
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
    val addedAt: Long,
)
