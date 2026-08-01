package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbGenreDto(
    @SerialName("id") val genreId: Int,
    val name: String = Constants.EMPTY_STRING,
)
