package com.sunstar.streamcompass.data.datasource.tmdb

internal object TmdbConstants {
    const val BASE_URL = "https://api.themoviedb.org/3"

    const val PATH_MOVIE = "movie"
    const val SEGMENT_NOW_PLAYING = "now_playing"
    const val SEGMENT_POPULAR = "popular"
    const val SEGMENT_TOP_RATED = "top_rated"
    const val SEGMENT_UPCOMING = "upcoming"
    const val SEGMENT_RECOMMENDATIONS = "recommendations"
    const val SEGMENT_REVIEWS = "reviews"

    const val PATH_TV = "tv"
    const val SEGMENT_AIRING_TODAY = "airing_today"
    const val SEGMENT_ON_THE_AIR = "on_the_air"
    const val SEGMENT_SEASON = "season"

    const val PATH_TRENDING = "trending"
    const val MEDIA_TYPE_ALL = "all"
    const val TIME_WINDOW_DAY = "day"
    const val MEDIA_TYPE_MOVIE = "movie"
    const val MEDIA_TYPE_TV = "tv"

    const val PARAM_API_KEY = "api_key"
    const val PARAM_LANGUAGE = "language"
    const val PARAM_PAGE = "page"
    const val PARAM_APPEND_TO_RESPONSE = "append_to_response"

    const val APPEND_IMAGES = "images"
    const val APPEND_RELEASE_DATES = "release_dates"
    const val APPEND_CONTENT_RATINGS = "content_ratings"
    const val APPEND_VIDEOS = "videos"
    const val APPEND_CREDITS = "credits"

    const val VIDEO_SITE_YOUTUBE = "YouTube"
    const val VIDEO_TYPE_TRAILER = "Trailer"

    const val DEFAULT_LANGUAGE = "en-US"

    const val PAGE_SIZE = 20

    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p"
    const val POSTER_SIZE = "w342"
    const val BACKDROP_SIZE = "w780"
    const val PROFILE_SIZE = "w185"
    const val LOGO_SIZE = "w300"
}
