package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbTvPageResponseDto(
    val page: Int,
    val results: List<TmdbTvSummaryDto>,
    val totalPages: Int,
    val totalResults: Int,
)
