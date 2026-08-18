package com.sunstar.streamcompass.data.converter

import com.sunstar.streamcompass.data.Constants
import com.sunstar.streamcompass.data.datasource.firestore.dto.FirestoreDeeplinkDto
import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalMovieHistoryEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvDetailEntity
import com.sunstar.streamcompass.data.datasource.local.entity.LocalTvHistoryEntity
import com.sunstar.streamcompass.data.datasource.streamingavailability.SaConstants
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaShowDto
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaStreamingOptionDto
import com.sunstar.streamcompass.data.datasource.tmdb.TmdbConstants
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbContentRatingsDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbImageDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbImagesDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbMovieDetailDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbReleaseDatesDto
import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbTvDetailDto
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Logo
import com.sunstar.streamcompass.domain.model.Stream.MovieStream
import com.sunstar.streamcompass.domain.model.Stream.TvStream
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
        logo = images.selectLogoUrl(locale = locale),
        backdrops = images.backdrops.map { it.toBackdropUrl() },
        certification = releaseDates.selectCertification(locale = locale),
    )

internal fun TmdbTvDetailDto.toEntity(locale: String): LocalTvDetailEntity =
    LocalTvDetailEntity(
        tmdbId = tmdbId,
        locale = locale,
        name = name,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        firstAirDate = firstAirDate,
        lastAirDate = lastAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalName = originalName,
        genres = genres.map { it.name },
        numberOfSeasons = numberOfSeasons,
        numberOfEpisodes = numberOfEpisodes,
        episodeRunTime = episodeRunTime,
        status = status,
        tagline = tagline,
        homepage = homepage,
        inProduction = inProduction,
        logo = images.selectLogoUrl(locale = locale),
        backdrops = images.backdrops.map { it.toBackdropUrl() },
        certification = contentRatings.selectCertification(locale = locale),
    )

// logo는 로케일 언어와 일치하는 것 → 언어 무관(투명) 로고 → 첫 번째 순으로 고른다.
private fun TmdbImagesDto.selectLogoUrl(locale: String): String {
    val languageCode = locale.substringBefore("-")
    val logo = logos.firstOrNull { it.iso6391 == languageCode }
        ?: logos.firstOrNull { null == it.iso6391 }
        ?: logos.firstOrNull()
    return logo?.toLogoUrl().orEmpty()
}

private fun TmdbImageDto.toBackdropUrl(): String =
    if (filePath.isEmpty()) filePath else "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.BACKDROP_SIZE}$filePath"

private fun TmdbImageDto.toLogoUrl(): String =
    if (filePath.isEmpty()) filePath else "${TmdbConstants.IMAGE_BASE_URL}/${TmdbConstants.LOGO_SIZE}$filePath"

// locale의 국가 코드(예: "en-US" → "US")와 일치하는 certification만 선택한다.
private fun TmdbReleaseDatesDto.selectCertification(locale: String): String =
    results
        .firstOrNull { it.country == locale.substringAfter("-", Constants.EMPTY_STRING) }
        ?.releaseDates
        ?.firstOrNull { it.certification.isNotEmpty() }
        ?.certification
        .orEmpty()

private fun TmdbContentRatingsDto.selectCertification(locale: String): String =
    results
        .firstOrNull { it.country == locale.substringAfter("-", Constants.EMPTY_STRING) }
        ?.rating
        .orEmpty()

internal fun MovieStream.toEntity(visitedAt: Long): LocalMovieHistoryEntity =
    LocalMovieHistoryEntity(
        tmdbId = tmdbId,
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
        visitedAt = visitedAt,
    )

internal fun TvStream.toEntity(visitedAt: Long): LocalTvHistoryEntity =
    LocalTvHistoryEntity(
        tmdbId = tmdbId,
        name = name,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalName = originalName,
        visitedAt = visitedAt,
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
