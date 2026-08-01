package com.sunstar.streamcompass.data.datasource.tmdb

import com.sunstar.streamcompass.data.BuildKonfig
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMoviePageResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

internal class TmdbDataSource(
    private val apiKey: String = BuildKonfig.TMDB_API_KEY,
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
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

    suspend fun getMovieDetail(
        tmdbId: Int,
        language: String = TmdbConstants.DEFAULT_LANGUAGE,
    ): TmdbMovieDetailDto =
        httpClient.get("${TmdbConstants.BASE_URL}/${TmdbConstants.PATH_MOVIE}/$tmdbId") {
            parameter(TmdbConstants.PARAM_API_KEY, apiKey)
            parameter(TmdbConstants.PARAM_LANGUAGE, language)
        }.body()

}
