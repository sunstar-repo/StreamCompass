package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbMoviePageResponseDto(
    val page: Int,
    val results: List<TmdbMovieSummaryDto>,
    val totalPages: Int,
    val totalResults: Int,
)
