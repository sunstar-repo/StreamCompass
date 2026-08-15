package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbReviewDto(
    val id: String = Constants.EMPTY_STRING,
    val author: String = Constants.EMPTY_STRING,
    @SerialName("author_details") val authorDetails: TmdbReviewAuthorDetailsDto = TmdbReviewAuthorDetailsDto(),
    val content: String = Constants.EMPTY_STRING,
    @SerialName("created_at") val createdAt: String = Constants.EMPTY_STRING,
)

@Serializable
internal data class TmdbReviewAuthorDetailsDto(
    val name: String = Constants.EMPTY_STRING,
    @SerialName("avatar_path") private val _avatarPath: String = Constants.EMPTY_STRING,
) {
    val avatarPath: String
        get() = when {
            _avatarPath.isEmpty() -> _avatarPath
            // TMDB gravatar avatars come back as "/https://..." — an already-absolute URL with a stray leading slash.
            _avatarPath.startsWith("/http") -> _avatarPath.removePrefix("/")
            else -> "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.PROFILE_SIZE}$_avatarPath"
        }
}
