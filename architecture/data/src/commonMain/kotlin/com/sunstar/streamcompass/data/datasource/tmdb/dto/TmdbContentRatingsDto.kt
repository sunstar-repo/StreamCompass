package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbContentRatingsDto(
    val results: List<TmdbContentRatingDto> = emptyList(),
)
