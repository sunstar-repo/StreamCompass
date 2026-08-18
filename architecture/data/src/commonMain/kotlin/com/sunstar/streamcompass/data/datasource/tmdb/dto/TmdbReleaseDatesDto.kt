package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbReleaseDatesDto(
    val results: List<TmdbReleaseDateCountryDto> = emptyList(),
)
