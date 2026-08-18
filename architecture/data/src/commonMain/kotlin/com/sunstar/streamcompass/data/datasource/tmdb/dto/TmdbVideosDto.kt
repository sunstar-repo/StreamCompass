package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbVideosDto(
    val results: List<TmdbVideoDto> = emptyList(),
)
