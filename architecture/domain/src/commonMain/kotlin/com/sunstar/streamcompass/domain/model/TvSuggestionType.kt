package com.sunstar.streamcompass.domain.model

sealed interface TvSuggestionType {
    data object AiringToday : TvSuggestionType

    data object OnTheAir : TvSuggestionType

    data object Popular : TvSuggestionType

    data object TopRated : TvSuggestionType
}
