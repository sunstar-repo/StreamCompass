package com.sunstar.streamcompass.data.converter

import com.sunstar.streamcompass.data.datasource.firestore.dto.FirestoreDeeplinkDto
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaConstants
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaShowDto
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaStreamingOptionDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Logo
import com.sunstar.streamcompass.domain.model.StreamType

internal fun TmdbMovieDetailDto.toEntity(locale: String): LocalMovieDetailEntity =
    LocalMovieDetailEntity(
        tmdbId = tmdbId,
        locale = locale,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        genres = genres.map { it.name },
        runtime = runtime,
        status = status,
        tagline = tagline,
        homepage = homepage,
        imdbId = imdbId,
        budget = budget,
        revenue = revenue,
        adult = adult,
        video = video,
    )

internal fun SaShowDto.toEntities(): List<LocalDeeplinkEntity> {
    val (streamType, parsedTmdbId) = parseTmdbId(tmdbId) ?: return emptyList()

    return streamingOptions.flatMap { (country, options) ->
        options.map { option -> option.toEntity(parsedTmdbId, streamType, country) }
    }
}

private fun parseTmdbId(raw: String): Pair<StreamType, Int>? {
    val segments = raw.split("/")
    if (segments.size != 2) return null

    val streamType =
        when (segments[0]) {
            SaConstants.PATH_MOVIE -> StreamType.Movie
            SaConstants.PATH_TV -> StreamType.Tv
            else -> return null
        }
    val tmdbId = segments[1].toIntOrNull() ?: return null

    return streamType to tmdbId
}

private fun SaStreamingOptionDto.toEntity(
    tmdbId: Int,
    streamType: StreamType,
    country: String,
): LocalDeeplinkEntity =
    LocalDeeplinkEntity(
        streamType = streamType.rawValue,
        tmdbId = tmdbId,
        country = country,
        service = service.name,
        link = link,
        videoLink = videoLink,
        lightThemeImage = service.imageSet.lightThemeImage,
        darkThemeImage = service.imageSet.darkThemeImage,
    )

internal fun FirestoreDeeplinkDto.toDeeplink(
    tmdbId: Int,
    streamType: StreamType,
    country: String,
    service: String
): Deeplink =
    Deeplink(
        streamType = streamType,
        tmdbId = tmdbId,
        locale = country,
        service = service,
        logo = Logo(lightThemeImage = lightThemeImage, darkThemeImage = darkThemeImage),
        link = link,
        videoLink = videoLink,
    )

internal fun SaShowDto.toFirestoreDeeplinkDtos(): Map<String, FirestoreDeeplinkDto> =
    streamingOptions.values.flatten().associate { option ->
        option.service.name to FirestoreDeeplinkDto(
            link = option.link,
            videoLink = option.videoLink,
            lightThemeImage = option.service.imageSet.lightThemeImage,
            darkThemeImage = option.service.imageSet.darkThemeImage,
        )
    }
