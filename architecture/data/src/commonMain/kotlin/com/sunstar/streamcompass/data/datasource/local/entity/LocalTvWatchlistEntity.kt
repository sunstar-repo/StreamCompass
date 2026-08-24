package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_watchlist")
internal data class LocalTvWatchlistEntity(
    @PrimaryKey val tmdbId: Int,
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
    val addedAt: Long,
)
