package com.sunstar.streamcompass.data.datasource.tmdb.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class TmdbSeasonDetailDto(
    val episodes: List<TmdbEpisodeDto> = emptyList(),
)
