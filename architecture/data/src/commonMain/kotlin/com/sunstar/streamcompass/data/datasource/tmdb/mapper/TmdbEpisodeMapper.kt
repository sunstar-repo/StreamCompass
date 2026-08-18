package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbEpisodeDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Episode

internal class TmdbEpisodeMapper : Mapper<TmdbEpisodeDto, Episode> {
    override fun map(source: TmdbEpisodeDto): Episode =
        Episode(
            episodeNumber = source.episodeNumber,
            name = source.name,
            overview = source.overview,
            stillPath = source.stillPath,
        )
}
