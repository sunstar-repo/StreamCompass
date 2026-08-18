package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbEpisodeDto(
    val episodeNumber: Int = Constants.UNSET_INT,
    val name: String = Constants.EMPTY_STRING,
    val overview: String = Constants.EMPTY_STRING,
    @SerialName("still_path") private val _stillPath: String = Constants.EMPTY_STRING,
) {
    val stillPath: String
        get() = if (_stillPath.isEmpty()) {
            _stillPath
        } else {
            "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.BACKDROP_SIZE}$_stillPath"
        }
}
