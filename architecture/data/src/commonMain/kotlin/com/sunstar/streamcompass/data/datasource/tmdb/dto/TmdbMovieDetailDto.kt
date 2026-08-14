package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbMovieDetailDto(
    @SerialName("id") val tmdbId: Int,
    val title: String = Constants.EMPTY_STRING,
    val overview: String = Constants.EMPTY_STRING,
    @SerialName("poster_path") private val _posterPath: String = Constants.EMPTY_STRING,
    @SerialName("backdrop_path") private val _backdropPath: String = Constants.EMPTY_STRING,
    val releaseDate: String = Constants.EMPTY_STRING,
    val voteAverage: Double = Constants.UNSET_DOUBLE,
    val voteCount: Int = Constants.UNSET_INT,
    val popularity: Double = Constants.UNSET_DOUBLE,
    val genres: List<TmdbGenreDto>,
    val runtime: Int = Constants.UNSET_INT,
    val status: String = Constants.EMPTY_STRING,
    val tagline: String = Constants.EMPTY_STRING,
    val originalLanguage: String = Constants.EMPTY_STRING,
    val originalTitle: String = Constants.EMPTY_STRING,
    val homepage: String = Constants.EMPTY_STRING,
    val imdbId: String = Constants.EMPTY_STRING,
    val budget: Long,
    val revenue: Long,
    val adult: Boolean,
    val video: Boolean,
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
