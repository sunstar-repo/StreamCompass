package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbImagesDto(
    val backdrops: List<TmdbImageDto> = emptyList(),
    val logos: List<TmdbImageDto> = emptyList(),
    val posters: List<TmdbImageDto> = emptyList(),
)
