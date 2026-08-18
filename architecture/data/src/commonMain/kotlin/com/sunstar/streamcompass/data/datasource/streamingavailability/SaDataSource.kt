package com.sunstar.streamcompass.data.datasource.streamingavailability

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaShowDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class SaDataSource(
    private val apiKey: String,
) {
    private val httpClient = HttpClient {
        expectSuccess = true
        defaultRequest {
            header(SaConstants.HEADER_API_KEY, apiKey)
        }
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    coerceInputValues = true
                },
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
        }
    }

    suspend fun getMovie(
        tmdbId: Int,
        country: String? = null,
    ): SaShowDto = getShow(SaConstants.PATH_MOVIE, tmdbId, country)

    suspend fun getTvShow(
        tmdbId: Int,
        country: String? = null,
    ): SaShowDto = getShow(SaConstants.PATH_TV, tmdbId, country)

    private suspend fun getShow(
        mediaType: String,
        tmdbId: Int,
        country: String?,
    ): SaShowDto =
        try {
            httpClient.get("${SaConstants.BASE_URL}/${SaConstants.PATH_SHOWS}/$mediaType/$tmdbId") {
                if (country != null) {
                    parameter(SaConstants.PARAM_COUNTRY, country)
                }
            }.body()
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.NotFound) {
                SaShowDto(id = Constants.EMPTY_STRING)
            } else {
                throw e
            }
        }
}
