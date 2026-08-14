package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbTrendingPageResponseDto(
    val page: Int,
    val results: List<TmdbTrendingItemDto>,
    val totalPages: Int,
    val totalResults: Int,
)
