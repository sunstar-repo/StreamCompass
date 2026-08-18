package com.sunstar.streamcompass.data.datasource.tmdb.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbReleaseDateCountryDto(
    @SerialName("iso_3166_1") val country: String = Constants.EMPTY_STRING,
    @SerialName("release_dates") val releaseDates: List<TmdbReleaseDateEntryDto> = emptyList(),
)
