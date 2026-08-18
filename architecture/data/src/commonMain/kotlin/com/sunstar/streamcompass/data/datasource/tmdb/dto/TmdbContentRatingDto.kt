package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbContentRatingDto(
    @SerialName("iso_3166_1") val country: String = Constants.EMPTY_STRING,
    val rating: String = Constants.EMPTY_STRING,
)
