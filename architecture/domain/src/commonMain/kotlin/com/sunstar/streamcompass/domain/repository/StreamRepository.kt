package com.sunstar.streamcompass.domain.repository

import androidx.paging.PagingData
import com.sunstar.streamcompass.domain.model.Review
import com.sunstar.streamcompass.domain.model.Stream
import com.sunstar.streamcompass.domain.model.StreamDetail
import com.sunstar.streamcompass.domain.model.StreamType
import com.sunstar.streamcompass.domain.model.SuggestionType
import kotlinx.coroutines.flow.Flow

interface StreamRepository {
    fun getSuggestionStreamFlow(type: SuggestionType): Flow<PagingData<Stream>>

    suspend fun getTrendingStream(): List<Stream>

    suspend fun getStreamDetail(tmdbId: Int, locale: String, streamType: StreamType): StreamDetail

    fun getRecommendationsStreamFlow(tmdbId: Int, streamType: StreamType): Flow<PagingData<Stream>>

    fun getReviewsStreamFlow(tmdbId: Int, streamType: StreamType): Flow<PagingData<Review>>

    suspend fun recordHistory(stream: Stream)

    fun getHistoryStreamFlow(streamType: StreamType): Flow<List<Stream>>

    suspend fun removeHistory(tmdbId: Int, streamType: StreamType)
}
