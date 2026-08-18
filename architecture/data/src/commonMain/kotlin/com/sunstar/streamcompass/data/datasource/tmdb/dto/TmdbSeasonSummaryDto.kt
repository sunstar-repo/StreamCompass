package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbSeasonSummaryDto(
    val seasonNumber: Int = Constants.UNSET_INT,
    val name: String = Constants.EMPTY_STRING,
    @SerialName("poster_path") private val _posterPath: String = Constants.EMPTY_STRING,
    val episodeCount: Int = Constants.UNSET_INT,
) {
    val posterPath: String
        get() = if (_posterPath.isEmpty()) {
            _posterPath
        } else {
            "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.POSTER_SIZE}$_posterPath"
        }
}
