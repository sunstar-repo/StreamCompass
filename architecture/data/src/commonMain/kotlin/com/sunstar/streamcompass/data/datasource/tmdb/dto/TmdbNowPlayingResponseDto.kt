package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
data class TmdbNowPlayingResponseDto(
    val page: Int,
    val results: List<TmdbMovieSummaryDto>,
    val totalPages: Int,
    val totalResults: Int,
)
