package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbVideoDto(
    val key: String = Constants.EMPTY_STRING,
    val site: String = Constants.EMPTY_STRING,
    val type: String = Constants.EMPTY_STRING,
)
