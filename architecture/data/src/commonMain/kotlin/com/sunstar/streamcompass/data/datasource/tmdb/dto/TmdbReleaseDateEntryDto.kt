package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbReleaseDateEntryDto(
    val certification: String = Constants.EMPTY_STRING,
)
