package com.sunstar.streamcompass.data.datasource.tmdb

import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMoviePageResponseDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbReviewPageResponseDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbSeasonDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTrendingPageResponseDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvPageResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class TmdbDataSource(
    private val apiKey: String,
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    coerceInputValues = true
                    namingStrategy = JsonNamingStrategy.SnakeCase
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
        }
    }

    suspend fun getNowPlaying(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMoviePageResponseDto = getMovieList(TmdbConstants.SEGMENT_NOW_PLAYING, page, language)

    suspend fun getPopular(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMoviePageResponseDto = getMovieList(TmdbConstants.SEGMENT_POPULAR, page, language)

    suspend fun getTopRated(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMoviePageResponseDto = getMovieList(TmdbConstants.SEGMENT_TOP_RATED, page, language)

    suspend fun getUpcoming(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMoviePageResponseDto = getMovieList(TmdbConstants.SEGMENT_UPCOMING, page, language)

    private suspend fun getMovieList(
        segment: String,
        page: Int,
        language: String,
    ): TmdbMoviePageResponseDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_MOVIE}/$segment") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    // 신작: 극장 또는 디지털로 이미 릴리즈됐고 구독(flatrate)으로 시청 가능한 영화를 최신 개봉일 순으로.
    suspend fun getNewMovies(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMoviePageResponseDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_DISCOVER}/${TmdbConstants.PATH_MOVIE}") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
            parameter(TmdbConstants.PARAM_WITH_RELEASE_TYPE, TmdbConstants.RELEASE_TYPE_DIGITAL)
            parameter(TmdbConstants.PARAM_SORT_BY, TmdbConstants.SORT_BY_PRIMARY_RELEASE_DATE_DESC)
            parameter(TmdbConstants.PARAM_PRIMARY_RELEASE_DATE_LTE, yesterday())
            parameter(TmdbConstants.PARAM_WITH_WATCH_MONETIZATION_TYPES, TmdbConstants.MONETIZATION_TYPE_FLATRATE)
            parameter(TmdbConstants.PARAM_WATCH_REGION, TmdbConstants.DEFAULT_REGION)
        }.body()

    suspend fun getAiringToday(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvPageResponseDto = getTvList(TmdbConstants.SEGMENT_AIRING_TODAY, page, language)

    suspend fun getOnTheAir(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvPageResponseDto = getTvList(TmdbConstants.SEGMENT_ON_THE_AIR, page, language)

    suspend fun getTvPopular(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvPageResponseDto = getTvList(TmdbConstants.SEGMENT_POPULAR, page, language)

    suspend fun getTvTopRated(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvPageResponseDto = getTvList(TmdbConstants.SEGMENT_TOP_RATED, page, language)

    private suspend fun getTvList(
        segment: String,
        page: Int,
        language: String,
    ): TmdbTvPageResponseDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_TV}/$segment") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    // 신작: 구독(flatrate) 스트리밍으로 시청 가능한 TV 시리즈를 최신 방영일 순으로.
    suspend fun getNewTvShows(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvPageResponseDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_DISCOVER}/${TmdbConstants.PATH_TV}") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
            parameter(TmdbConstants.PARAM_WITH_WATCH_MONETIZATION_TYPES, TmdbConstants.MONETIZATION_TYPE_FLATRATE)
            parameter(TmdbConstants.PARAM_WATCH_REGION, TmdbConstants.DEFAULT_REGION)
            parameter(TmdbConstants.PARAM_SORT_BY, TmdbConstants.SORT_BY_FIRST_AIR_DATE_DESC)
        }.body()

    suspend fun getTrendingAllDay(
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTrendingPageResponseDto =
        httpClient.get(
            "${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_TRENDING}" +
                    "/${TmdbConstants.MEDIA_TYPE_ALL}/${TmdbConstants.TIME_WINDOW_DAY}"
        ) {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    suspend fun getMovieDetail(
        tmdbId: Int,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMovieDetailDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_MOVIE}/$tmdbId") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(
                TmdbConstants.PARAM_APPEND_TO_RESPONSE,
                "${TmdbConstants.APPEND_IMAGES},${TmdbConstants.APPEND_RELEASE_DATES}," +
                        "${TmdbConstants.APPEND_VIDEOS},${TmdbConstants.APPEND_CREDITS}"
            )
        }.body()

    suspend fun getTvDetail(
        tmdbId: Int,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvDetailDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_TV}/$tmdbId") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(
                TmdbConstants.PARAM_APPEND_TO_RESPONSE,
                "${TmdbConstants.APPEND_IMAGES},${TmdbConstants.APPEND_CONTENT_RATINGS}," +
                        "${TmdbConstants.APPEND_VIDEOS},${TmdbConstants.APPEND_CREDITS}"
            )
        }.body()

    suspend fun getMovieRecommendations(
        tmdbId: Int,
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMoviePageResponseDto =
        httpClient.get(
            "${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_MOVIE}/$tmdbId/${TmdbConstants.SEGMENT_RECOMMENDATIONS}"
        ) {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    suspend fun getMovieReviews(
        tmdbId: Int,
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbReviewPageResponseDto =
        httpClient.get(
            "${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_MOVIE}/$tmdbId/${TmdbConstants.SEGMENT_REVIEWS}"
        ) {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    suspend fun getSeasonDetail(
        tmdbId: Int,
        seasonNumber: Int,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbSeasonDetailDto =
        httpClient.get(
            "${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_TV}/$tmdbId/${TmdbConstants.SEGMENT_SEASON}/$seasonNumber"
        ) {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
        }.body()

    suspend fun getTvRecommendations(
        tmdbId: Int,
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbTvPageResponseDto =
        httpClient.get(
            "${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_TV}/$tmdbId/${TmdbConstants.SEGMENT_RECOMMENDATIONS}"
        ) {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    suspend fun getTvReviews(
        tmdbId: Int,
        page: Int = 1,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbReviewPageResponseDto =
        httpClient.get(
            "${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_TV}/$tmdbId/${TmdbConstants.SEGMENT_REVIEWS}"
        ) {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
            parameter(TmdbConstants.PARAM_PAGE, page)
        }.body()

    // 신작 필터의 상한값 — 개봉 예정작(미래 날짜)이 섞이지 않도록 하루 전까지만 허용한다.
    @OptIn(ExperimentalTime::class)
    private fun yesterday(): String =
        Clock.System.todayIn(TimeZone.currentSystemDefault()).minus(DatePeriod(days = 1)).toString()
}
