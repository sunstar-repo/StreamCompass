package com.sunstar.streamcompass.domain.model

sealed interface SuggestionType {
    sealed interface Movie : SuggestionType {
        data object NowPlaying : Movie

        data object Popular : Movie

        data object TopRated : Movie

        data object Upcoming : Movie
    }

    sealed interface Tv : SuggestionType {
        data object AiringToday : Tv

        data object OnTheAir : Tv

        data object Popular : Tv

        data object TopRated : Tv
    }
}
