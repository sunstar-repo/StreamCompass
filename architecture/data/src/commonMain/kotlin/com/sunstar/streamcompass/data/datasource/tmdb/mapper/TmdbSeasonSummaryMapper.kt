package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbSeasonSummaryDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Season

internal class TmdbSeasonSummaryMapper : Mapper<TmdbSeasonSummaryDto, Season> {
    override fun map(source: TmdbSeasonSummaryDto): Season =
        Season(
            seasonNumber = source.seasonNumber,
            name = source.name,
            posterPath = source.posterPath,
            episodeCount = source.episodeCount,
        )
}
