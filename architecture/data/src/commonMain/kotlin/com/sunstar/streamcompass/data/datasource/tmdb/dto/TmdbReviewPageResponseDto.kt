package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbReviewPageResponseDto(
    val page: Int,
    val results: List<TmdbReviewDto>,
    val totalPages: Int,
    val totalResults: Int,
)
