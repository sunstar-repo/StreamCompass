package com.sunstar.streamcompass.data.datasource.tmdb

internal object TmdbConstants {
    const val BASE_URL = "https://api.themoviedb.org/3"

    const val PATH_MOVIE = "movie"
    const val SEGMENT_NOW_PLAYING = "now_playing"
    const val SEGMENT_POPULAR = "popular"
    const val SEGMENT_TOP_RATED = "top_rated"
    const val SEGMENT_UPCOMING = "upcoming"

    const val PARAM_API_KEY = "api_key"
    const val PARAM_LANGUAGE = "language"
    const val PARAM_PAGE = "page"

    const val DEFAULT_LANGUAGE = "en-US"

    const val PAGE_SIZE = 20

    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
    const val POSTER_SIZE = "w342"

    fun posterUrl(posterPath: String): String =
        if (posterPath.isEmpty()) posterPath else "$IMAGE_BASE_URL/$POSTER_SIZE$posterPath"
}
