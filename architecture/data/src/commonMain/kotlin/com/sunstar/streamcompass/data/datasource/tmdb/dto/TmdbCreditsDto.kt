package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbCreditsDto(
    val cast: List<TmdbCastDto> = emptyList(),
    val crew: List<TmdbCrewDto> = emptyList(),
)
