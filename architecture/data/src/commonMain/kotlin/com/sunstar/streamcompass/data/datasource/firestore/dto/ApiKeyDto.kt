package com.sunstar.streamcompass.data.datasource.firestore.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiKeyDto(
    @SerialName("TMDB_API_KEY") val tmdbApiKey: String = Constants.EMPTY_STRING,
    @SerialName("SA_API_KEY") val saApiKey: String = Constants.EMPTY_STRING,
)
