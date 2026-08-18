package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbImageDto(
    @SerialName("file_path") val filePath: String = Constants.EMPTY_STRING,
    val iso6391: String? = null,
)
