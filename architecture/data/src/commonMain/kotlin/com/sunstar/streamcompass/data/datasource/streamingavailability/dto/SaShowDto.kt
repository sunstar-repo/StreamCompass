package com.sunstar.streamcompass.data.datasource.streamingavailability.dto

import com.sunstar.streamcompass.data.Constants
import kotlinx.serialization.Serializable

@Serializable
internal data class SaShowDto(
    val id: String,
    val itemType: String = Constants.EMPTY_STRING,
    val showType: String = Constants.EMPTY_STRING,
    val imdbId: String = Constants.EMPTY_STRING,
    val tmdbId: String = Constants.EMPTY_STRING,
    val title: String = Constants.EMPTY_STRING,
    val overview: String = Constants.EMPTY_STRING,
    val originalTitle: String = Constants.EMPTY_STRING,
    val releaseYear: Int = Constants.UNSET_INT,
    val firstAirYear: Int = Constants.UNSET_INT,
    val lastAirYear: Int = Constants.UNSET_INT,
    val rating: Int = Constants.UNSET_INT,
    val runtime: Int = Constants.UNSET_INT,
    val seasonCount: Int = Constants.UNSET_INT,
    val episodeCount: Int = Constants.UNSET_INT,
    val streamingOptions: Map<String, List<SaStreamingOptionDto>> = emptyMap(),
)
