package com.sunstar.streamcompass.data.datasource.local.mapper

import com.sunstar.streamcompass.data.datasource.local.entity.LocalDeeplinkEntity
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Deeplink
import com.sunstar.streamcompass.domain.model.Logo
import com.sunstar.streamcompass.domain.model.StreamType

internal class LocalDeeplinkEntityMapper : Mapper<LocalDeeplinkEntity, Deeplink> {
    override fun map(source: LocalDeeplinkEntity): Deeplink =
        Deeplink(
            link = source.link,
            videoLink = source.videoLink,
            tmdbId = source.tmdbId,
            streamType = StreamType.from(rawValue = source.streamType),
            locale = source.locale,
            service = source.service,
            logo = Logo(
                lightThemeImage = source.lightThemeImage,
                darkThemeImage = source.darkThemeImage
            ),
        )
}
