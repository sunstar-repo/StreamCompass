package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbTrendingItemDto(
    @SerialName("id") val tmdbId: Int = Constants.UNSET_INT,
    val mediaType: String = Constants.EMPTY_STRING,
    val title: String = Constants.EMPTY_STRING,
    val name: String = Constants.EMPTY_STRING,
    val originalTitle: String = Constants.EMPTY_STRING,
    val originalName: String = Constants.EMPTY_STRING,
    val overview: String = Constants.EMPTY_STRING,
    @SerialName("poster_path") private val _posterPath: String = Constants.EMPTY_STRING,
    @SerialName("backdrop_path") private val _backdropPath: String = Constants.EMPTY_STRING,
    val releaseDate: String = Constants.EMPTY_STRING,
    val firstAirDate: String = Constants.EMPTY_STRING,
    val voteAverage: Double = Constants.UNSET_DOUBLE,
    val voteCount: Int = Constants.UNSET_INT,
    val popularity: Double = Constants.UNSET_DOUBLE,
    val originalLanguage: String = Constants.EMPTY_STRING,
) {
    val posterPath: String
        get() = if (_posterPath.isEmpty()) {
            _posterPath
        } else {
            "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.POSTER_SIZE}$_posterPath"
        }

    val backdropPath: String
        get() = if (_backdropPath.isEmpty()) {
            _backdropPath
        } else {
            "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.BACKDROP_SIZE}$_backdropPath"
        }
}
