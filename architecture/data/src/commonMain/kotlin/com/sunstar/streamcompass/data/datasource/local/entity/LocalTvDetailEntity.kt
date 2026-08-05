package com.sunstar.streamcompass.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_detail")
internal data class LocalTvDetailEntity(
    @PrimaryKey val tmdbId: Int,
    val name: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val firstAirDate: String,
    val lastAirDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val originalLanguage: String,
    val originalName: String,
    val genres: List<String>,
    val numberOfSeasons: Int,
    val numberOfEpisodes: Int,
    val episodeRunTime: List<Int>,
    val status: String,
    val tagline: String,
    val homepage: String,
    val inProduction: Boolean,
)
