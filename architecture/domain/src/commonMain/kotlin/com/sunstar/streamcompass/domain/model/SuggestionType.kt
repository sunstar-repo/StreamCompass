package com.sunstar.streamcompass.domain.model

sealed interface SuggestionType {
    data object NowPlaying : SuggestionType

    data object Popular : SuggestionType

    data object TopRated : SuggestionType

    data object Upcoming : SuggestionType
}
