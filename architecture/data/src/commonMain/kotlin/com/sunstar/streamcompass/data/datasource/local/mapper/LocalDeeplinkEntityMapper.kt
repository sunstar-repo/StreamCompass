package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Logo
import com.sunstar.streamcompass.domain.model.StreamType

internal class LocalDeeplinkEntityMapper : Mapper<LocalDeeplinkEntity, Deeplink> {
    override fun map(source: LocalDeeplinkEntity): Deeplink =
        Deeplink(
            streamType = StreamType.from(rawValue = source.streamType),
            tmdbId = source.tmdbId,
            locale = source.country,
            service = source.service,
            logo = Logo(
                lightThemeImage = source.lightThemeImage,
                darkThemeImage = source.darkThemeImage
            ),
            link = source.link,
            videoLink = source.videoLink,
        )
}
