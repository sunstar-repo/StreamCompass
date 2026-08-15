package com.sunstar.streamcompass.data.datasource.tmdb.mapper

import com.sunstar.streamcompass.data.datasource.tmdb.dto.TmdbReviewDto
import com.sunstar.streamcompass.domain.mapper.Mapper
import com.sunstar.streamcompass.domain.model.Review

internal class TmdbReviewMapper : Mapper<TmdbReviewDto, Review> {
    override fun map(source: TmdbReviewDto): Review =
        Review(
            id = source.id,
            authorName = source.authorDetails.name.ifEmpty { source.author },
            avatarPath = source.authorDetails.avatarPath,
            content = source.content,
            createdAt = source.createdAt,
        )
}
