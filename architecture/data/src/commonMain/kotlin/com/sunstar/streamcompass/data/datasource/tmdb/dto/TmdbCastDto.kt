package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbCastDto(
    val name: String = Constants.EMPTY_STRING,
    val character: String = Constants.EMPTY_STRING,
    @SerialName("profile_path") private val _profilePath: String = Constants.EMPTY_STRING,
) {
    val profilePath: String
        get() = if (_profilePath.isEmpty()) {
            _profilePath
        } else {
            "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.PROFILE_SIZE}$_profilePath"
        }
}
