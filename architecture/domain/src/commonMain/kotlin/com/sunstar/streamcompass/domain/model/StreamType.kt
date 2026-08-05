package com.sunstar.streamcompass.domain.model

//deeplink/streamtype
sealed class StreamType(val rawValue: String) {
    data object Movie : StreamType("movie")

    data object Tv : StreamType("tv")

    companion object {
        fun from(rawValue: String): StreamType =
            when (rawValue) {
                Movie.rawValue -> Movie
                Tv.rawValue -> Tv
                else -> error("Unknown stream type: $rawValue")
            }
    }
}
