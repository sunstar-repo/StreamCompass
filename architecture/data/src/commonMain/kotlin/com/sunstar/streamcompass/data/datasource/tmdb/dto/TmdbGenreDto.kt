package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TmdbGenreDto(
    @SerialName("id") val genreId: Int,
    val name: String,
)
