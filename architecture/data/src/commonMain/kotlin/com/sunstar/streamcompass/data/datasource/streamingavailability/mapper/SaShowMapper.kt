package com.sunstar.streamcompass.data.datasource.streamingavailability.mapper

import com.sunstar.streamcompass.data.datasource.streamingavailability.SaConstants
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaShowDto
import com.sunstar.streamcompass.data.datasource.streamingavailability.dto.SaStreamingOptionDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Logo
import com.sunstar.streamcompass.domain.model.StreamType

internal class SaShowMapper : Mapper<SaShowDto, List<Deeplink>> {
    override fun map(source: SaShowDto): List<Deeplink> {
        val (streamType, tmdbId) = parseTmdbId(source.tmdbId) ?: return emptyList()

        return source.streamingOptions.flatMap { (locale, options) ->
            options.map { option -> option.toDeeplink(tmdbId, streamType, locale) }
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

    private fun SaStreamingOptionDto.toDeeplink(
        tmdbId: Int,
        streamType: StreamType,
        locale: String,
    ): Deeplink =
        Deeplink(
            link = link,
            videoLink = videoLink,
            tmdbId = tmdbId,
            streamType = streamType,
            locale = locale,
            service = service.name,
            logo =
                Logo(
                    lightThemeImage = service.imageSet.lightThemeImage,
                    darkThemeImage = service.imageSet.darkThemeImage,
                ),
        )
}
